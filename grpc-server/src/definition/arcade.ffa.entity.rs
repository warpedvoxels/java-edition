#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct FfaPlayerStats {
    pub uuid: ::uuid::Uuid,
    pub kills: i32,
    pub deaths: i32,
    pub assists: i32,
    pub killstreak: i32,
    pub longest_killstreak: i32,
}
