use actix_web::{web, Scope};
use serde::{Deserialize, Serialize};

mod v1;

pub fn v1() -> Scope {
    web::scope("/api/v1").service(v1::player::find)
}

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct PageInfo {
    pub page: Option<u64>,
    pub limit: Option<u64>,
}
