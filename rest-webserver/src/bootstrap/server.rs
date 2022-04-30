use std::{io::Error, net::SocketAddr, sync::Arc};

use crate::app::WebServerStateData;
use crate::{api, middleware::create_identity_service};
use actix_cors::Cors;
use actix_web::{http::header, middleware::Logger, web::Data as AppData, App, HttpServer};

pub async fn build(state: WebServerStateData, ip: SocketAddr) -> Result<(), Error> {
    let server = HttpServer::new(move || {
        App::new()
            .wrap(create_identity_service(&state))
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
    log::debug!("Successfully started the server on {}!", ip);
    server
}
