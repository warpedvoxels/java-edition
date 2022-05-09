use anyhow::{Result, Context};
use env_logger::builder;

pub fn init() -> Result<()> {
    if std::env::var("RUST_LOG").is_err() {
        std::env::set_var("RUST_LOG", "debug");
    }
    builder()
        .filter_level(log::LevelFilter::Debug)
        .try_init()
        .context("Failed to initialize the logging system.")
}