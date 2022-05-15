#![feature(const_type_name)]

macro_rules! server {
    ($name:ident) => {
        #[cfg(feature = "server")]
        pub mod $name;
    };
}

pub mod codec;
pub mod definition;
pub use grpc_server_common::datatype;

server!(app);
server!(bootstrap);
server!(routing);

#[path = "macro/mod.rs"]
#[cfg(feature = "server")]
pub mod macros;
