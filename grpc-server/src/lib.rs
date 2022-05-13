#![feature(const_type_name)]

pub mod definition;
pub mod codec;
pub mod routing;
pub mod bootstrap;
pub mod app;
pub mod datatype;

#[path = "macro/mod.rs"]
pub mod macros;