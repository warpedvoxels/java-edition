use clap::StructOpt;
use hexalite::internal;
use hexalite::CommandLineOptions;

fn main() {
    match CommandLineOptions::parse().command {
        hexalite::HexaliteCommands::Init { path } => internal::init(path),
        _ => println!("Not yet implemented."),
    }
}
