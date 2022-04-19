extern crate lazy_static;

use webserver::app::WebserverStateData;
use webserver::bootstrap::*;
use webserver::entity::{Player, Entity};

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    logger::init();
    let settings = settings::build();
    let pool = database::build(&settings).await;

    if let Some(error) = pool.as_ref().err() {
        panic!("{}", error);
    }

    let state = WebserverStateData {
        pool: pool.unwrap(),
        settings,
    };

    Player::up(&state).await.expect("Failed to create the player table.");
    
    let ip = state.settings.ip();
    server::build(state, ip).await
}
