use actix_web::{web, Either, HttpResponse, Result};

pub type RestResult<T> = Result<Either<HttpResponse, web::Json<T>>>;

pub mod player;
