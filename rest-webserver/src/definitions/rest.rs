#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq)]
pub struct Authorization {
    pub sub: ::core::option::Option<::uuid::Uuid>,
    pub exp: ::chrono::DateTime<::chrono::Utc>,
    pub is_internal: bool,
}
