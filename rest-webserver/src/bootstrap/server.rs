use std::{io::Error, net::SocketAddr};

use crate::app::WebserverState;
use actix_web::{middleware::Logger, web::Data as AppData, App, HttpServer};

pub async fn build(state: WebserverState, ip: SocketAddr) -> Result<(), Error> {
    HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
            .app_data(AppData::new(state.to_owned()))
    })
    .bind(ip)
    .expect("Failed to bind the server.")
    .run()
    .await
}