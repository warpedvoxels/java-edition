use std::ffi;
use std::str::FromStr;

use cbor4ii::serde as cbor;
use libc::{c_char, size_t};
use tonic::transport::Channel;
use uuid::Uuid;

use grpc_server::definition::protocol::{
    player::PlayerClient, player_data_request::Id, PlayerDataRequest,
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
        let vec = cbor::to_vec(Vec::new(), value).unwrap();
        let len = vec.len() as size_t;
        let bytes = vec.into_boxed_slice();
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
        Vec::from_raw_parts(buf.data as *mut u8, buf.len, buf.len);
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
            id: Some(Id::Uuid(uuid)),
        };
        let reply = get_player_service()
            .retrieve_data(req)
            .await
            .expect("Failed to retrieve player");
        let buf = ByteBuf::from(&reply.get_ref().player);
        println!("{buf:?}");
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
        let player: Player = cbor::from_slice((*dummy).vec().as_slice()).unwrap();
        println!("{player:?}");
    }
}
