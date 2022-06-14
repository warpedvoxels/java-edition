use crate::HexaliteCommand;

mod build;
mod init;
mod run;
mod utils;
use anyhow::{Context, Result};
pub use utils::*;
use xshell::Shell;

pub async fn run(command: HexaliteCommand) -> Result<()> {
    let sh = Shell::new().context("Failed to create a shell session.")?;
    match command {
        HexaliteCommand::Init { path } => init::init(path).await,
        HexaliteCommand::Build { module } => build::build(&sh, module).await,
        HexaliteCommand::Purpur => run::minecraft(&sh).await,
        HexaliteCommand::WebServer => run::webserver(&sh).await,
        HexaliteCommand::ResourcePack => run::resource_pack(&sh).await,
    }
}
