#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct RestRole {
    #[prost(string, tag="1")]
    pub id: ::prost::alloc::string::String,
    #[prost(string, tag="2")]
    pub unicode_characters: ::prost::alloc::string::String,
    #[prost(string, tag="3")]
    pub color: ::prost::alloc::string::String,
    #[prost(string, tag="4")]
    pub tab_list_index: ::prost::alloc::string::String,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct RestUser {
    #[prost(message, optional, tag="1")]
    pub uuid: ::core::option::Option<::uuid::Uuid>,
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
    #[prost(message, repeated, tag="7")]
    pub roles: ::prost::alloc::vec::Vec<RestRole>,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct Authorization {
    #[prost(message, optional, tag="1")]
    pub sub: ::core::option::Option<::uuid::Uuid>,
    #[prost(message, optional, tag="2")]
    pub exp: ::core::option::Option<::prost_types::Timestamp>,
    #[prost(bool, tag="3")]
    pub is_internal: bool,
}
