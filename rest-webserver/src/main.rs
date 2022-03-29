use std::net::SocketAddr;

use actix_web::{HttpServer, App, middleware::Logger};
use config::Config;

mod config;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    fast_log::init(fast_log::config::Config::new().console()).expect("logger init failed");
    let config: Config = config::load();
    let server_address: SocketAddr = format!("{}:{}", &config.ip, &config.port).parse().expect("couldn't resolve domain");
    
    HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
    })
    .bind(server_address)?
    .run()
    .await
}