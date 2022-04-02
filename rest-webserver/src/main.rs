use std::sync::Arc;

use actix_web::{middleware::Logger, web::Data as AppData, App, HttpServer};

extern crate lazy_static;

use hexalite::{app::WebserverState, settings};
use sea_orm::Database;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    fast_log::init(fast_log::config::Config::new().console()).expect("logger init failed");
 
    let settings = settings::read();
    let url = settings.services.database.url();

    let state = WebserverState {
        connection: Arc::new(
            Database::connect(&url)
                .await
                .expect("database connection failed"),
        ),
    };

    HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
            .app_data(AppData::new(state.to_owned()))
    })
    .bind(settings.ip())?
    .run()
    .await
}
