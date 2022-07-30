use std::str::FromStr;
use libc::{c_char, size_t};
use tonic::transport::Channel;
use uuid::Uuid;

use grpc_server::definition::{
    protocol::{player::PlayerClient, PlayerDataRequest},
};
use grpc_server::definition::protocol::{PlayerCreateRequest, PlayerDataPatchRequest};

use crate::{as_str, ByteBuf, runtime};

static mut SERVICE: Option<PlayerClient<Channel>> = None;

pub async unsafe fn connect(uri: String) {
    SERVICE = Some(PlayerClient::connect(uri).await.unwrap());
}

pub unsafe fn shutdown() {
    SERVICE = None;
}

pub unsafe fn get<'a>() -> &'a mut PlayerClient<Channel> {
    SERVICE.as_mut().expect("Player service not initialized")
}

#[no_mangle]
pub unsafe extern "C" fn retrieve_player_by_uuid(id: *const c_char) -> *const ByteBuf {
    runtime::exec(async move {
        let uuid = Uuid::from_str(as_str(id)).expect("Failed to parse given UUID");
        let req = PlayerDataRequest {
            id: Some(grpc_server::definition::protocol::player_data_request::Id::Uuid(uuid)),
        };
        let reply = get()
            .retrieve_data(req)
            .await
            .expect("Failed to retrieve player by UUID.");
        let buf = ByteBuf::from(&reply.get_ref().data);
        Box::into_raw(Box::new(buf))
    })
}

#[no_mangle]
pub unsafe extern "C" fn retrieve_player_by_last_username(username: *const c_char) -> *const ByteBuf {
    runtime::exec(async move {
        let req = PlayerDataRequest {
            id: grpc_server::definition::protocol::player_data_request::Id::Username(as_str(username).into()).into(),
        };
        let reply = get()
            .retrieve_data(req)
            .await
            .expect("Failed to retrieve player by last username.");
        let buf = ByteBuf::from(&reply.get_ref().data);
        Box::into_raw(Box::new(buf))
    })
}

#[no_mangle]
pub unsafe extern "C" fn modify_player_data(data: *const u8, len: size_t) -> *const ByteBuf {
    runtime::exec(async move {
        let slice = std::slice::from_raw_parts(data, len);
        let patch: PlayerDataPatchRequest = cbor4ii::serde::from_slice(slice).expect("Failed to deserialize patch.");
        let reply = get().modify_data(patch).await.expect("Failed to modify player data.");
        let buf = ByteBuf::from(&reply.get_ref().data);
        Box::into_raw(Box::new(buf))
    })
}

#[no_mangle]
pub unsafe extern "C" fn create_player(data: *const u8, len: size_t) -> *const ByteBuf {
    runtime::exec(async move {
        let slice = std::slice::from_raw_parts(data, len);
        let data: PlayerCreateRequest = cbor4ii::serde::from_slice(slice).expect("Failed to deserialize create payload.");
        let reply = get().create(data).await.expect("Failed to create player.");
        let buf = ByteBuf::from(&reply.get_ref().data);
        Box::into_raw(Box::new(buf))
    })
}
