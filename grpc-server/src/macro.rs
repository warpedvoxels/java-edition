#[macro_export]
macro_rules! import {
    ($name:ident) => {
        mod $name;
        pub use $name::*;
    };
}

#[macro_export]
macro_rules! impl_nullable_prisma_ident {
    ($var:expr, $name:ident) => {
        $var.$name
    };
    ($var:expr, $name:ident $value:expr) => {
        $value
    };
}

#[macro_export]
macro_rules! prisma_attr {
    ($var:expr => $($name:ident $(: $value:expr)?),*) => {
        {
            let mut attributes = Vec::new();
            $(
                if $var.$name.is_some() {
                    attributes.push($crate::prisma::player::$name::set($crate::impl_nullable_prisma_ident!($var, $name $($value)?)));
                }
            )*
            attributes
        }
    }
}

pub use import;
pub use prisma_attr;
