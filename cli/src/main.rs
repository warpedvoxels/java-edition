#![feature(async_closure)]

use clap::StructOpt;
use hexalite::internal;
use hexalite::CommandLineOptions;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let options = CommandLineOptions::parse();
    internal::run(options.command).await
}
