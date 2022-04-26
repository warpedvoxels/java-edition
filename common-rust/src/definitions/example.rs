#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct Example {
    #[prost(string, required, tag="1")]
    pub name: ::prost::alloc::string::String,
    #[prost(int32, required, tag="2")]
    pub id: i32,
    #[prost(string, required, tag="3")]
    pub email: ::prost::alloc::string::String,
}
