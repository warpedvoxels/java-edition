#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct Authorization {
    pub sub: ::core::option::Option<::uuid::Uuid>,
    pub exp: ::core::option::Option<::chrono::DateTime<::chrono::Utc>>,
    pub is_internal: bool,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct RestRole {
    pub id: ::prost::alloc::string::String,
    pub unicode_characters: ::prost::alloc::string::String,
    pub color: ::prost::alloc::string::String,
    pub tab_list_index: ::prost::alloc::string::String,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct RestUser {
    pub uuid: ::core::option::Option<::uuid::Uuid>,
    pub hexes: i32,
    pub last_username: ::prost::alloc::string::String,
    pub last_seen: ::core::option::Option<::chrono::DateTime<::chrono::Utc>>,
    pub created_at: ::core::option::Option<::chrono::DateTime<::chrono::Utc>>,
    pub updated_at: ::core::option::Option<::chrono::DateTime<::chrono::Utc>>,
    pub roles: ::prost::alloc::vec::Vec<RestRole>,
}
