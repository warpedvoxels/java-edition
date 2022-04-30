use actix_web::{web, HttpResponse, Either, Result};

pub type RestResult<T> = Result<Either<HttpResponse, web::Json<T>>>;

pub mod player;