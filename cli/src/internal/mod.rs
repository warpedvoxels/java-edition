use crate::HexaliteCommand;

mod build;
mod init;
mod run;
mod utils;
use anyhow::Result;
pub use utils::*;

pub async fn run(command: HexaliteCommand) -> Result<()> {
    match command {
        HexaliteCommand::Init { path } => init::init(path).await,
        HexaliteCommand::Build { module } => build::build(module).await,
        HexaliteCommand::Purpur => run::minecraft().await,
        HexaliteCommand::WebServer => run::webserver().await,
        HexaliteCommand::ResourcePack => run::resource_pack().await,
    }
}
