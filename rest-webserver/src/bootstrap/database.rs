use std::time::Duration;

use anyhow::{Context, Result};

use crate::{
    app::{PoolOptions, SqlPool},
    settings::WebserverSettings, entity::{Player, Entity},
};

pub async fn build(settings: &WebserverSettings) -> Result<SqlPool> {
    let url = settings.services.database.url();

    let options = PoolOptions::new().connect_timeout(Duration::from_nanos(30));
    let connection = options.connect(&url).await.context("Failed to connect to the database.")?;
    
    log::info!("Connected to the database succesfully!");
    
    Player::up(&connection)
        .await
        .context("Failed to create the player table.")?;

    Ok(connection)
}
