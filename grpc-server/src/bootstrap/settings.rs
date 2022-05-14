use anyhow::Result;
use hexalite_common::settings::{self, HexaliteSettings};

pub fn init() -> Result<HexaliteSettings> {
    let settings = settings::read().expect("Failed to read the settings.");
    settings.write().unwrap();
    Ok(settings)
}
