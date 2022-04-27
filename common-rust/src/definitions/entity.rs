#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct User {
    #[prost(message, optional, tag="1")]
    pub uuid: ::core::option::Option<super::datatypes::Uuid>,
    #[prost(int32, tag="2")]
    pub hexes: i32,
    #[prost(string, tag="3")]
    pub last_username: ::prost::alloc::string::String,
    #[prost(message, optional, tag="4")]
    pub last_seen: ::core::option::Option<::prost_types::Timestamp>,
    #[prost(message, optional, tag="5")]
    pub created_at: ::core::option::Option<::prost_types::Timestamp>,
    #[prost(message, optional, tag="6")]
    pub updated_at: ::core::option::Option<::prost_types::Timestamp>,
    #[prost(enumeration="UserRank", repeated, tag="7")]
    pub ranks: ::prost::alloc::vec::Vec<i32>,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash, PartialOrd, Ord, ::prost::Enumeration)]
#[repr(i32)]
pub enum UserRank {
    Elite = 0,
    Spectacle = 1,
    Sentinel = 2,
    Guardian = 3,
    Artist = 4,
    Developer = 5,
}
