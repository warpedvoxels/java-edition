use anyhow::Result;
use hexalite_common::settings::{self, HexaliteSettings};

pub fn init() -> Result<HexaliteSettings> {
    let settings = settings::read().expect("Failed to read the settings.");
    settings.write().unwrap();
    println!("\n\n\x1b[2J\x1b[1;30m Server settings loaded from ~/.hexalite/settings.toml\n\n");
    Ok(settings)
}
