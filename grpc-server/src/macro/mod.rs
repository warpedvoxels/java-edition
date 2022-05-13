#[macro_export]
macro_rules! import {
    ($name:ident) => {
        mod $name;
        pub use $name::*;
    };
}

import!(sql);

pub(crate) use import;