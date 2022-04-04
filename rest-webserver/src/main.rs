extern crate lazy_static;

use hexalite::app::WebserverState;
use hexalite::bootstrap::*;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    logger::init();
    let settings = settings::build();
    let database = database::build(&settings).await;

    if let Some(error) = database.as_ref().err() {
        panic!("{}", error);
    }

    let state = WebserverState {
        database: database.unwrap(),
        settings,
    };
    let ip = state.settings.ip();
    server::build(state, ip).await
}
