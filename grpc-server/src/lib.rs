#![feature(const_type_name)]

pub mod app;
pub mod bootstrap;
pub mod codec;
pub mod definition;
pub mod routing;
pub use grpc_server_common::datatype;

#[path = "macro/mod.rs"]
pub mod macros;
