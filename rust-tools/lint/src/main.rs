use xshell::{cmd, Shell};
use anyhow::{Result, Context};

fn main() -> Result<()> {
    let sh = Shell::new()?;

    cmd!(sh, "cargo fmt --all -- --check")
        .run()
        .context("Please run 'cargo fmt --all' to format your code.")?;
    cmd!(sh, "cargo clippy --workspace --fix --allow-dirty --all-targets --all-features -- -D warnings -A clippy::type_complexity -W clippy::doc_markdown")
        .run()
        .context("Please fix clippy errors in output above.")?;
    Ok(())
}