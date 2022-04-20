use clap::StructOpt;
use hexalite::internal;
use hexalite::CommandLineOptions;

fn main() {
    let options = CommandLineOptions::parse();
    internal::run(options.command)
}
