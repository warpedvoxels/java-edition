extern crate lazy_static;

use hexalite::app::WebserverState;
use hexalite::bootstrap::*;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    logger::init();
    let settings = settings::build();
    let database = database::build(&settings).await;

    let state = WebserverState { database, settings };
    let ip = state.settings.ip();
    server::build(state, ip).await
}
