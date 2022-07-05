use crate::prisma::{new_client, PrismaClient};
use anyhow::{Context, Result};
use hexalite_common::settings::HexaliteSettings;
use terminal_spinners::{SpinnerBuilder, DOTS};

pub async fn init(settings: &HexaliteSettings) -> Result<PrismaClient> {
    let handle = SpinnerBuilder::new().spinner(&DOTS).prefix(" ").text("Connecting to PostgreSQL").start();
    std::env::set_var("DATABASE_URL", settings.grpc.services.postgres.to_string());
    let client = new_client()
        .await
        .context("Failed to initialize the Postgres client.")?;
    log::info!("Successfully connected to PostgreSQL.");
    handle.done();
    Ok(client)
}
