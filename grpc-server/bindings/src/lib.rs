use std::str::FromStr;

use cbor4ii::serde as cbor;
use grpc_server::definition::protocol::{
    player::PlayerClient, player_data_request::Id, PlayerDataRequest,
};
use libc::c_char;
use tonic::transport::Channel;
use uuid::Uuid;

pub static mut PLAYER_SERVICE: Option<PlayerClient<Channel>> = None;

unsafe fn as_str<'a>(pointer: *const c_char) -> &'a str {
    std::ffi::CStr::from_ptr(pointer).to_str().unwrap()
}

#[no_mangle]
pub async extern "C" fn init_services(uri: *const c_char) {
    unsafe {
        let uri = as_str(uri);
        PLAYER_SERVICE = Some(PlayerClient::connect(uri).await.unwrap());
    }
}

pub unsafe fn get_player_service() -> &mut PlayerClient<Channel> {
    PLAYER_SERVICE
        .as_mut()
        .expect("Player service not initialized")
}

#[no_mangle]
pub async extern "C" fn retrieve_player_by_uuid(id: *const c_char) -> *const u8 {
    let reply = unsafe {
        let id = as_str(id);
        let uuid = Uuid::from_str(id).expect("Failed to parse given UUID");
        let req = PlayerDataRequest {
            id: Some(Id::Uuid(uuid)),
        };
        get_player_service()
            .retrieve_data(req)
            .await
            .expect("Failed to retrieve player")
    };
    let bytes = cbor::to_vec(Vec::new(), &reply.get_ref().player).unwrap();
    bytes.as_ptr()
}
