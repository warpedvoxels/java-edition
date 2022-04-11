use actix_web::{web, Scope};

mod v1;

pub fn v1() -> Scope {
    web::scope("/api/v1").service(v1::player::find)
}
