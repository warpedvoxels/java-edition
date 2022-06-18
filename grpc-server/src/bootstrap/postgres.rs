use crate::prisma::{new_client, PrismaClient};
use anyhow::{Context, Result};
use hexalite_common::settings::HexaliteSettings;

pub async fn init(settings: &HexaliteSettings) -> Result<PrismaClient> {
    std::env::set_var("DATABASE_URL", settings.grpc.services.postgres.to_string());
    let client = new_client()
        .await
        .context("Failed to initialize the Postgres client.")?;
    log::info!("Successfully connected to PostgreSQL.");
    Ok(client)
}
