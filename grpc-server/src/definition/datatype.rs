#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Id {
    pub data: ::core::option::Option<id::Data>,
}
/// Nested message and enum types in `Id`.
pub mod id {
    #[derive(serde::Serialize, serde::Deserialize)]
    #[serde(rename_all = "snake_case")]
    #[derive(Debug, Clone, PartialEq)]
    pub enum Data {
        Uuid(::uuid::Uuid),
        Username(::prost::alloc::string::String),
    }
}
