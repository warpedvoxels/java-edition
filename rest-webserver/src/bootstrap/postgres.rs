use anyhow::{Context, Result};
use bb8_postgres::{bb8::Pool, PostgresConnectionManager};
use tokio_postgres::NoTls;

use crate::{
    app::SqlPool,
    entity::{Entity, Player},
};
use hexalite_common::settings::GrpcSettings;

pub async fn build(settings: &GrpcSettings) -> Result<SqlPool> {
    let url = settings.services.postgres.url();
    let manager = PostgresConnectionManager::new_from_stringlike(url, NoTls)
        .context("Failed to create a Postgres connection manager.")?;

    let pool = Pool::builder()
        .max_lifetime(
            Some(settings
                .services
                .postgres
                .pool
                .max_lifetime
                .to_std()
                .unwrap()),
        )
        .idle_timeout(
            Some(settings
                .services
                .postgres
                .pool
                .idle_timeout
                .to_std()
                .unwrap()),
        )
        .connection_timeout(
            settings
                .services
                .postgres
                .pool
                .connection_timeout
                .to_std()
                .unwrap(),
        )
        .build(manager)
        .await
        .context("Failed to create a Postgres pool.")?;

    log::debug!("Connected to the database succesfully!");

    Player::up(&pool)
        .await
        .context("Failed to create the player table.")?;

    Ok(pool)
}
