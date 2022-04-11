use std::{io::Error, net::SocketAddr, sync::Arc};

use crate::api;
use crate::app::WebserverStateData;
use actix_web::{middleware::Logger, web::Data as AppData, App, HttpServer};

pub async fn build(state: WebserverStateData, ip: SocketAddr) -> Result<(), Error> {
    let server = HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
            .app_data(AppData::new(Arc::new(state.to_owned())))
            .service(api::v1())
    })
    .bind(ip)
    .expect("Failed to bind the server.")
    .run()
    .await;
    log::info!("Successfully started the server on {}!", ip);
    server
}
