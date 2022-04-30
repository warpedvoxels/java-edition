use hexalite_common::dirs::get_hexalite_dir_path;

use super::*;
use anyhow::{Context, Result};

pub async fn webserver() -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let compiled = hexalite.join("compiled");
    // webserver on unix-like systems and webserver.exe on windows
    let path = if cfg!(target_os = "windows") {
        compiled.join("webserver.exe")
    } else {
        compiled.join("webserver")
    };
    run_command(
        path.to_str()
            .context("Failed to retrieve the compiled webserver path.")?,
        &[],
    )
    .await;
    Ok(())
}

pub async fn resource_pack() -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let compiled = hexalite.join("compiled");
    let path = compiled.join("resource-pack");
    let path = path
        .to_str()
        .context("Failed to retrieve the compiled resource pack path.")?;
    run_command(path, &[]).await;
    Ok(())
}

pub async fn minecraft() -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let run = hexalite.join("run");
    let path = run.join("purpur.jar");
    let path = path
        .to_str()
        .expect("Failed to retrieve the path to the Minecraft server.");
    run_command("java", &["-Xmx2G", "-jar", path]).await;
    Ok(())
}
