use anyhow::{Context, Result};
use hexalite_common::settings::HexaliteSettings;

pub type SqlType = sqlx::Postgres;

pub type SqlPool = sqlx::Pool<SqlType>;

pub type SqlPoolOptions = sqlx::pool::PoolOptions<SqlType>;

pub async fn init(settings: &HexaliteSettings) -> Result<SqlPool> {
    let settings = &settings.grpc.services.postgres;

    let pool = SqlPoolOptions::new()
        .max_connections(settings.pool.max_connections)
        .max_lifetime(settings.pool.max_lifetime.to_std().unwrap())
        .idle_timeout(settings.pool.idle_timeout.to_std().unwrap())
        .connect_timeout(settings.pool.connection_timeout.to_std().unwrap())
        .connect(&settings.url())
        .await
        .context("Failed to connect to PostgreSQL.")?;
    sqlx::migrate!().run(&pool).await?;

    log::info!("Successfully connected to PostgreSQL.");

    Ok(pool)
}
