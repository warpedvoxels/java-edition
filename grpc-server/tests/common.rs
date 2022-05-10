use anyhow::{Context, Result};
use grpc_server::bootstrap::{logging, settings};
use hexalite_common::settings::HexaliteSettings;

pub fn common() -> Result<HexaliteSettings> {
    logging::init().unwrap();
    let settings = settings::init().context("Failed to initialize settings.")?;
    Ok(settings)
}
