use crate::HexaliteCommand;

mod build;
mod constants;
mod init;
mod utils;
mod run;

pub use constants::*;
pub use utils::*;

pub fn run(command: HexaliteCommand) {
    match command {
        HexaliteCommand::Init { path } => init::init(path),
        HexaliteCommand::Build { module } => build::build(module),
        HexaliteCommand::Purpur => run::minecraft(),
        HexaliteCommand::Webserver => run::webserver(),
        HexaliteCommand::ResourcePack => run::resource_pack(),
    }
}
