#![allow(clippy::missing_safety_doc)]

use libc::c_char;

pub mod runtime;
pub mod util;
pub mod player;

pub use util::*;
pub use player::*;

//noinspection HttpUrlsUsage
#[no_mangle]
pub unsafe extern "C" fn init_services(uri: *const c_char, prefer_ssl: bool) {
    runtime::exec(async move {
        let uri = http_uri(uri, prefer_ssl);
        player::connect(uri).await;
    });
}

#[no_mangle]
pub unsafe extern "C" fn shutdown() {
    player::shutdown();
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
