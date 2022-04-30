

use anyhow::{Context, Result};
use bb8_postgres::{bb8::Pool, PostgresConnectionManager};
use tokio_postgres::NoTls;

use crate::{
    entity::{Entity, Player},
    settings::WebServerSettings, app::SqlPool,
};

pub async fn build(settings: &WebServerSettings) -> Result<SqlPool> {
    let url = settings.services.database.url();
    let manager = PostgresConnectionManager::new_from_stringlike(url, NoTls)
        .context("Failed to create a Postgres connection manager.")?;

    let pool = Pool::builder()
        .max_size(settings.services.database.pool.max_size)
        .min_idle(settings.services.database.pool.min_idle)
        .max_lifetime(settings.services.database.pool.max_lifetime.map(|duration| duration.to_std().unwrap()))
        .idle_timeout(settings.services.database.pool.idle_timeout.map(|duration| duration.to_std().unwrap()))
        .connection_timeout(settings.services.database.pool.connection_timeout.to_std().unwrap())
        .reaper_rate(settings.services.database.pool.reaper_rate.to_std().unwrap())
        .build(manager)
        .await
        .context("Failed to create a Postgres pool.")?; 

    log::debug!("Connected to the database succesfully!");

    Player::up(&pool)
        .await
        .context("Failed to create the player table.")?;
    
    Ok(pool)
}
