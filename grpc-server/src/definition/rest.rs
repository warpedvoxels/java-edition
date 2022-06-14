#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Authorization {
    pub sub: ::core::option::Option<::uuid::Uuid>,
    pub exp: ::chrono::NaiveDateTime,
    pub is_internal: bool,
}
