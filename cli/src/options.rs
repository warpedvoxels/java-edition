use std::path::PathBuf;

use clap::{Parser, Subcommand};

#[derive(Parser, Debug)]
#[clap(author, version, about, long_about = None)]
#[clap(propagate_version = true)]
pub struct CommandLineOptions {
    #[clap(subcommand)]
    pub command: HexaliteCommands,
}

#[derive(Subcommand, Debug)]
pub enum HexaliteCommands {
    /// Create all necessary symbolic links for development and production environments
    Init {
        // The path to the Hexalite Network source code.
        path: PathBuf,
    },

    /// Build the command-line interface, webserver and all plugins and symlink them to their respective directories
    Build {
        // The arcade module to be linked. If empty, only the webserver will be linked. You can find a list of modules at the arcade directory.
        module: Option<String>,
    },

    /// Set up a Purpur server environment at ~/.hexalite/run or run it if it already exists
    Purpur,

    /// Compose all Docker containers related to the Hexalite Network
    Docker,

    /// Create a symbolic link the command-line interface to the PATH on Unix-like systems
    Symlink,

    /// Run a compiled binary of the rest webserver. Build command is required to be ran first.
    Webserver,

    /// Generates the resource pack for the Hexalite Network. Build command is required to be ran first.
    ResourcePack,
}
