pub trait ExportFields {
    fn fields() -> phf::Map<&'static str, &'static str>;
}

pub use common_macros::*;