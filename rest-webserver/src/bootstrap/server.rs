use std::{io::Error, net::SocketAddr, sync::Arc};

use crate::api;
use crate::app::WebserverStateData;
use actix_cors::Cors;
use actix_web::{http::header, middleware::Logger, web::Data as AppData, App, HttpServer};

pub async fn build(state: WebserverStateData, ip: SocketAddr) -> Result<(), Error> {
    let server = HttpServer::new(move || {
        App::new()
            .wrap(
                Cors::default()
                    .allowed_origin(format!("http://localhost:{}", ip.port()).as_str())
                    .allowed_methods(vec!["GET", "POST", "PUT", "DELETE"])
                    .allowed_headers(vec![
                        header::AUTHORIZATION,
                        header::ACCEPT,
                        header::CONTENT_TYPE,
                    ])
                    .supports_credentials()
                    .max_age(3600),
            )
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
