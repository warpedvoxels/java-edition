#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct Authorization {
    pub sub: ::core::option::Option<::uuid::Uuid>,
    pub exp: ::chrono::DateTime<::chrono::Utc>,
    pub is_internal: bool,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct RestRole {
    pub id: ::prost::alloc::string::String,
    pub unicode_character: ::prost::alloc::string::String,
    pub color: ::prost::alloc::string::String,
    pub tab_list_index: ::prost::alloc::string::String,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct RestPlayer {
    pub uuid: ::uuid::Uuid,
    pub hexes: i32,
    pub last_username: ::prost::alloc::string::String,
    pub last_seen: ::chrono::DateTime<::chrono::Utc>,
    pub created_at: ::chrono::DateTime<::chrono::Utc>,
    pub updated_at: ::chrono::DateTime<::chrono::Utc>,
}
