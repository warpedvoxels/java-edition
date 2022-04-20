use crate::HexaliteCommand;

mod build;
mod constants;
mod init;
mod utils;

pub use constants::*;
pub use utils::*;

pub fn run(command: HexaliteCommand) {
    match command {
        HexaliteCommand::Init { path } => init::init(path),
        HexaliteCommand::Build { module } => build::build(module),
        _ => println!("Not yet implemented."),
    }
}
