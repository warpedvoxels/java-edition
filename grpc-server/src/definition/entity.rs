#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Clan {
    pub id: u32,
    pub name: ::prost::alloc::string::String,
    pub tag: ::prost::alloc::string::String,
    pub points: u32,
    pub owner_id: ::uuid::Uuid,
    pub created_at: ::chrono::NaiveDateTime,
    pub updated_at: ::chrono::NaiveDateTime,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct ClanMember {
    pub player_id: ::uuid::Uuid,
    pub clan_id: u32,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct Player {
    pub uuid: ::uuid::Uuid,
    pub hexes: u32,
    pub last_username: ::prost::alloc::string::String,
    pub last_seen: ::chrono::NaiveDateTime,
    pub created_at: ::chrono::NaiveDateTime,
    pub updated_at: ::chrono::NaiveDateTime,
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct UserRole {
    pub player_id: ::uuid::Uuid,
    pub clan_id: ::prost::alloc::string::String,
}
