use clap::Parser;

#[derive(Parser, Debug)]
#[clap(author, version, about, long_about = None)]
pub struct CommandLineOptions {
    #[clap(global = true, short, long)]
    pub build: Option<String>,
}
