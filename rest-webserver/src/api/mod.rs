use actix_web::{web, Scope, dev::{ServiceFactory, ServiceRequest, ServiceResponse}, body::{EitherBody, BoxBody}};
use serde::{Deserialize, Serialize};

use crate::middleware::Authentication;

mod v1;

pub fn v1() -> Scope<impl ServiceFactory<ServiceRequest, Response = ServiceResponse<EitherBody<BoxBody>>, Error = actix_web::Error, Config = (), InitError = ()>> {
    let internal_auth_middleware = Authentication { needs_internal: true };

    web::scope("/api/v1")
        .wrap(internal_auth_middleware)
        .service(v1::player::find)
}

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct PageInfo {
    pub page: Option<u64>,
    pub limit: Option<u64>,
}
