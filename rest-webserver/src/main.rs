#![feature(async_closure)]

extern crate lazy_static;

use webserver::app::WebServerStateData;
use webserver::bootstrap::*;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    logger::init();
    let settings = settings::build();

    let state = WebServerStateData {
        postgres: postgres::build(&settings.webserver).await.unwrap(),
        redis: redis::build(&settings.webserver).await.unwrap(),
        rabbitmq: rabbitmq::build(&settings.webserver).await.unwrap(),
        settings,
    };

    let ip = state.settings.webserver.ip();
    server::build(state, ip).await
}
