#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Player {
    pub uuid: ::uuid::Uuid,
    pub hexes: i32,
    pub last_username: ::prost::alloc::string::String,
    pub last_seen: ::chrono::NaiveDateTime,
    pub created_at: ::chrono::NaiveDateTime,
    pub updated_at: ::chrono::NaiveDateTime,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Role {
    pub id: ::prost::alloc::string::String,
    pub unicode_character: ::prost::alloc::string::String,
    pub color: ::prost::alloc::string::String,
    pub tab_list_index: ::prost::alloc::string::String,
}
