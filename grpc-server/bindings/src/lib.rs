use grpc_server::definition::protocol::{greeter::GreeterClient, player::PlayerClient};
use libc::c_char;
use tonic::transport::Channel;

pub static mut PLAYER_SERVICE: Option<PlayerClient<Channel>> = None;
pub static mut GREETER_SERVICE: Option<GreeterClient<Channel>> = None;

#[no_mangle]
pub async extern "C" fn init_services(uri: *const c_char) {
    unsafe {
        let uri = std::ffi::CStr::from_ptr(uri).to_str().unwrap();
        PLAYER_SERVICE = Some(PlayerClient::connect(uri).await.unwrap());
    }
}
