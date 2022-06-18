#![feature(const_type_name)]

macro_rules! server {
    ($name:ident) => {
        #[cfg(feature = "server")]
        pub mod $name;
    };
}

pub mod codec;
pub mod definition;

server!(app);
server!(bootstrap);
server!(routing);
server!(prisma);

#[path = "macro.rs"]
#[cfg(feature = "server")]
pub mod macros;