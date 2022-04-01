use actix_web::{middleware::Logger, App, HttpServer};
extern crate lazy_static;
use hexalite::settings;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    fast_log::init(fast_log::config::Config::new().console()).expect("logger init failed");

    HttpServer::new(|| App::new().wrap(Logger::default()))
        .bind(settings::read().ip())?
        .run()
        .await
}
