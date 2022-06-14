use anyhow::{Context, Result};
use hexalite_common::settings::HexaliteSettings;
use crate::prisma::{PrismaClient, new_client};

pub async fn init(settings: &HexaliteSettings) -> Result<PrismaClient> {
    let settings = &settings.grpc.services.postgres;
    let client = new_client().await?;
    log::info!("Successfully connected to PostgreSQL.");
    Ok(client)
}
