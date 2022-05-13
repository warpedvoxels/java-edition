#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[sea_query::enum_def(suffix = "TypeDef")]
#[derive(Clone, PartialEq)]
pub struct Role {
    pub id: ::prost::alloc::string::String,
    pub unicode_character: ::prost::alloc::string::String,
    pub color: ::prost::alloc::string::String,
    pub tab_list_index: ::prost::alloc::string::String,
    pub permissions: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[sea_query::enum_def(suffix = "TypeDef")]
#[derive(Clone, PartialEq)]
pub struct Player {
    pub uuid: ::uuid::Uuid,
    pub hexes: i32,
    pub last_username: super::datatype::Username,
    pub last_seen: ::chrono::DateTime<::chrono::Utc>,
    pub created_at: ::chrono::DateTime<::chrono::Utc>,
    pub updated_at: ::chrono::DateTime<::chrono::Utc>,
}
