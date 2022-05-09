#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Example {
    pub name: ::core::option::Option<::prost::alloc::string::String>,
    pub id: i32,
    pub email: ::prost::alloc::string::String,
}
