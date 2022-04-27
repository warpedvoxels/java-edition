#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct Uuid {
    #[prost(bytes="vec", tag="1")]
    pub value: ::prost::alloc::vec::Vec<u8>,
}
