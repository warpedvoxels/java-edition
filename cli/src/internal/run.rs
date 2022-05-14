use hexalite_common::dirs::get_hexalite_dir_path;

use super::*;
use anyhow::{Context, Result};

pub async fn webserver(sh: &Shell) -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let compiled = hexalite.join("compiled");
    // webserver on unix-like systems and webserver.exe on windows
    let path = if cfg!(target_os = "windows") {
        compiled.join("webserver.exe")
    } else {
        compiled.join("webserver")
    };
    let path = path
        .to_str()
        .context("Failed to retrieve the compiled webserver path.")?;

    xshell::cmd!(sh, "{path}")
        .run()
        .context("Failed to run the webserver.")
}

pub async fn resource_pack(sh: &Shell) -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let compiled = hexalite.join("compiled");
    let path = if cfg!(target_os = "windows") {
        compiled.join("resource-pack.exe")
    } else {
        compiled.join("resource-pack")
    };
    let path = path
        .to_str()
        .context("Failed to retrieve the compiled resource pack path.")?;
    xshell::cmd!(sh, "{path}")
        .run()
        .context("Failed to run the resource pack generator.")
}

pub async fn minecraft(sh: &Shell) -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let run = hexalite.join("run");
    let path = run.join("purpur.jar");
    let path = path
        .to_str()
        .expect("Failed to retrieve the path to the Minecraft server.");
    xshell::cmd!(sh, "java -Xmx2G -jar {path}")
        .run()
        .context(" Failed to run the Minecraft server.")
}
