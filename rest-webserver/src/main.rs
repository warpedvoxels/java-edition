#![feature(async_closure)]

extern crate lazy_static;

use webserver::app::WebServerStateData;
use webserver::bootstrap::*;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    logger::init();
    let settings = settings::build();
    let pool = database::build(&settings.webserver).await;
    let _redis = redis::build(&settings.webserver).await;

    if let Some(error) = pool.as_ref().err() {
        panic!("{}", error);
    }

    let state = WebServerStateData {
        pool: pool.unwrap(),
        settings,
    };

    let ip = state.settings.webserver.ip();
    server::build(state, ip).await
}
