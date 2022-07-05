#![allow(clippy::missing_safety_doc)]

use std::ffi;
use std::str::FromStr;

use libc::{c_char, size_t};
use tonic::transport::Channel;
use uuid::Uuid;

use grpc_server::definition::{
    datatype::id::Data as Id,
    protocol::{player::PlayerClient, PlayerDataRequest},
};

pub mod runtime;

static mut PLAYER_SERVICE: Option<PlayerClient<Channel>> = None;

#[repr(C)]
#[derive(Debug)]
pub struct ByteBuf {
    pub len: size_t,
    pub data: *const u8,
}

impl<T> From<&T> for ByteBuf
where
    T: serde::ser::Serialize,
{
    fn from(value: &T) -> Self {
        let buffer = cbor4ii::serde::to_vec(Vec::new(), value).unwrap();
        let len = buffer.len() as size_t;
        let bytes = buffer.into_boxed_slice();
        println!("{}", hex::encode(&bytes));
        let data = Box::into_raw(bytes) as *const u8;
        Self { len, data }
    }
}

impl ByteBuf {
    pub unsafe fn vec(&self) -> Vec<u8> {
        Vec::from_raw_parts(self.data as *mut u8, self.len as usize, self.len as usize)
    }
}

unsafe fn as_str<'a>(pointer: *const c_char) -> &'a str {
    ffi::CStr::from_ptr(pointer).to_str().unwrap()
}

#[no_mangle]
pub unsafe extern "C" fn free_buf(buf: *const ByteBuf) {
    if !buf.is_null() {
        let ptr = buf as *mut ByteBuf;
        let buf = Box::from_raw(ptr);
        buf.vec();
    }
}

#[no_mangle]
pub unsafe extern "C" fn get_buf_data(buf: *const ByteBuf) -> *const u8 {
    (*buf).data
}

#[no_mangle]
pub unsafe extern "C" fn get_buf_len(buf: *const ByteBuf) -> size_t {
    (*buf).len
}

//noinspection HttpUrlsUsage
#[no_mangle]
pub unsafe extern "C" fn init_services(uri: *const c_char, prefer_ssl: bool) {
    runtime::exec(async move {
        let uri = if prefer_ssl {
            format!("https://{}", as_str(uri))
        } else {
            format!("http://{}", as_str(uri))
        };
        PLAYER_SERVICE = Some(PlayerClient::connect(uri).await.unwrap());
    });
}

#[no_mangle]
pub unsafe extern "C" fn shutdown() {
    PLAYER_SERVICE = None;
}

unsafe fn get_player_service<'a>() -> &'a mut PlayerClient<Channel> {
    PLAYER_SERVICE
        .as_mut()
        .expect("Player service not initialized")
}

#[no_mangle]
pub unsafe extern "C" fn retrieve_player_by_uuid(id: *const c_char) -> *const ByteBuf {
    runtime::exec(async move {
        let id = as_str(id);
        let uuid = Uuid::from_str(id).expect("Failed to parse given UUID");
        let req = PlayerDataRequest {
            id: Id::Uuid(uuid).into(),
        };
        let reply = get_player_service()
            .retrieve_data(req)
            .await
            .expect("Failed to retrieve player");
        let buf = ByteBuf::from(&reply.get_ref().data);
        Box::into_raw(Box::new(buf))
    })
}

#[test]
pub fn receiving() {
    use grpc_server::definition::entity::Player;
    use std::ffi::CString;

    fn str(literal: &str) -> CString {
        CString::new(literal).unwrap()
    }

    unsafe {
        init_services(str("127.0.0.1:50051").into_raw(), false);
        let dummy = retrieve_player_by_uuid(str("2652b3d5-2c36-4582-a955-484f6edbdf9b").into_raw());
        let player: Player = cbor4ii::serde::from_slice((*dummy).vec().as_slice()).unwrap();
        println!("{player:?}");
    }
}
