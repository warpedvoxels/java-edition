use grpc_server_common::datatype::*;
use std::collections::HashMap;

macro_rules! data_types {
    ($($kind:ty => $name:tt),* $(,)?) => {
        fn data_types() -> HashMap<&'static str, String> {
            let mut map: HashMap<&'static str, String> = HashMap::new();
            $(
                map.insert(std::any::type_name::<$kind>(), format!("{} NOT NULL", $name));
                map.insert(std::any::type_name::<Option<$kind>>(), $name.to_string());
            )*
            map
        }
    }
}

data_types! {
    String => "TEXT",
    Username => "VARCHAR(16)",
    uuid::Uuid => "UUID",
    u16 => "SMALLSERIAL",
    i16 => "SMALLINT",
    u32 => "SERIAL",
    u64 => "BIGSERIAL",
    i32 => "INTEGER",
    i64 => "BIGINT",
    usize => "BIGSERIAL",
    isize => "SERIAL",
    f32 => "REAL",
    f64 => "DOUBLE PRECISION",
    bool => "BOOLEAN",
    chrono::NaiveDateTime => "TIMESTAMP DEFAULT NOW()",
    serde_json::value::Value => "JSONB",
}
