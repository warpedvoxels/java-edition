use std::{fs, path::Path};

use anyhow::Context;

use hexalite_common::dirs::{get_hexalite_dir_path, get_source_path};

use crate::internal::*;
use crate::{compiled_file, file_from_src, file_name};

lazy_static::lazy_static! {
    static ref WATERFALL_MODULES: Vec<&'static str> = vec![];
}

fn link_plugin(src: &Path, symlinked_plugins_directory: &Path) {
    let name = src
        .file_name()
        .unwrap()
        .to_str()
        .unwrap()
        .split('/')
        .last()
        .unwrap();
    let reobf_name = format!("{}-reobf.jar", name);
    let file = src.join("build").join("libs").join(reobf_name);
    let file_name = file.file_name().unwrap().to_str().unwrap();
    let dest = symlinked_plugins_directory.join(format!("linked-{}", file_name));
    println!(
        "Creating symbolic link {} to {}",
        src.to_str().unwrap(),
        dest.to_str().unwrap()
    );
    use_handling(&file, &dest, |src, dest| symlink::symlink_file(src, dest));
}

pub async fn build(sh: &Shell, module: Option<String>) -> Result<()> {
    let hexalite = get_hexalite_dir_path();
    let compiled_path = hexalite.join("compiled");
    if let Err(err) = fs::create_dir_all(&compiled_path) {
        handle_dir_error(&compiled_path, &compiled_path, err);
    }

    let src_path = get_source_path().context("Failed to get source path")?;
    if !src_path.exists() {
        panic!("The source path does not exist. Please make sure to populate the command-line interface first by using `hexalite init`.");
    }

    let resource_pack_name = file_name!("resource-pack");
    let webserver_name = file_name!("webserver");
    let grpc_server_name = file_name!("grpc-server");
    let cli_name = file_name!("hexalite");
    let client_native_name = if cfg!(target_os = "windows") {
        "grpc_server_bindings.dll"
    } else if cfg!(target_os = "macos") {
        "libgrpc_server_bindings.dylib"
    } else {
        "libgrpc_server_bindings.so"
    };

    use_handling(
        &file_from_src!(webserver_name),
        &compiled_file!(webserver_name),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );
    use_handling(
        &file_from_src!(resource_pack_name),
        &compiled_file!(resource_pack_name),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );
    use_handling(
        &file_from_src!(cli_name),
        &compiled_file!(cli_name),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );
    use_handling(
        &file_from_src!(grpc_server_name),
        &compiled_file!(grpc_server_name),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );
    use_handling(
        &src_path.join("target/release").join(&client_native_name),
        &compiled_path.join(&client_native_name),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );
    sh.change_dir(&hexalite_common::dirs::get_source_path().unwrap());
    xshell::cmd!(sh, "cargo build --release").run().unwrap();

    // gradlew if unix or gradlew.bat if windows
    let gradlew = src_path.join(if cfg!(target_os = "windows") {
        "gradlew.bat"
    } else {
        "gradlew"
    });
    let gradlew = gradlew
        .to_str()
        .context("Could not get the gradle path as string.")?;
    xshell::cmd!(sh, "{gradlew} build")
        .run()
        .context("Failed to build the project (Gradle).")?;

    if let Some(module) = module {
        let module = module.trim().to_lowercase();
        let path = src_path.join("arcade").join(&module);
        if !path.exists() {
            anyhow::bail!("The arcade module {} does not exist.", module);
        }
        let is_waterfall = WATERFALL_MODULES.contains(&module.as_str());
        if is_waterfall {
            anyhow::bail!("Waterfall is not yet supported.");
        }
        let symlinked_plugins_directory = src_path.join("run").join("plugins");
        let dependencies_path = src_path.join("arcade").join(&module).join("DEPENDENCIES");
        if dependencies_path.exists() {
            let content = fs::read_to_string(dependencies_path)
                .context("Failed to read the dependencies file.")?;

            for line in content.lines() {
                let dependency = line.trim();
                if dependency.is_empty() {
                    continue;
                }
                let src = src_path.join(dependency);
                link_plugin(&src, &symlinked_plugins_directory);
            }
        }
        link_plugin(
            &src_path.join("kraken").join("purpur"),
            &symlinked_plugins_directory,
        );
        link_plugin(
            &src_path.join("arcade").join(&module),
            &symlinked_plugins_directory,
        );
    }

    Ok(())
}
