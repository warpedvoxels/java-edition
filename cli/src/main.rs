#![feature(async_closure)]

use anyhow::Result;
use clap::StructOpt;
use hexalite::internal;
use hexalite::CommandLineOptions;

#[tokio::main]
async fn main() -> Result<()> {
    let options = CommandLineOptions::parse();
    internal::run(options.command).await
}
