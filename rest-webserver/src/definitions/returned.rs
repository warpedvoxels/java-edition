#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct ReturnedPlayer {
    pub uuid: ::uuid::Uuid,
    pub hexes: i32,
    pub last_username: ::prost::alloc::string::String,
    pub last_seen: ::chrono::DateTime<::chrono::Utc>,
    pub created_at: ::chrono::DateTime<::chrono::Utc>,
    pub updated_at: ::chrono::DateTime<::chrono::Utc>,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct ReturnedRole {
    pub id: ::prost::alloc::string::String,
    pub unicode_character: ::prost::alloc::string::String,
    pub color: ::prost::alloc::string::String,
    pub tab_list_index: ::prost::alloc::string::String,
}
