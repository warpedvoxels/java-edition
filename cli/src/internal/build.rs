use std::{fs, path::Path};

use hexalite_common::dirs::{get_hexalite_dir_path, get_source_path};

use crate::internal::*;

lazy_static::lazy_static! {
    static ref MANIFESTS: Vec<&'static str> = vec!["cli/Cargo.toml", "resource-pack/Cargo.toml"]; //, "rest-webserver/Cargo.toml"];
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
    use_handling(&file, &dest, |src, dest| symlink::symlink_file(src, dest));
}

pub fn build(module: Option<String>) {
    let hexalite = get_hexalite_dir_path();
    let compiled_path = hexalite.join("compiled");
    if let Err(err) = fs::create_dir_all(&compiled_path) {
        handle_dir_error(&compiled_path, &compiled_path, err);
    }
    let src_path = get_source_path();
    if !src_path.exists() {
        panic!("The source path does not exist. Please make sure to populate the command-line interface first by using `hexalite init`.");
    }
    use_handling(
        &src_path.join("resource-pack-generator/build/libs/rp-shaded.jar"),
        &compiled_path.join("resource-pack-generator.jar"),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );

    for manifest_path in &*MANIFESTS {
        let manifest_path = src_path.join(manifest_path);
        let manifest_path = manifest_path
            .to_str()
            .expect("Failed to get the manifest path as string.");
        run_command(
            "cargo",
            &["build", "--release", "--manifest-path", manifest_path],
        );
    }
    run_command(
        // gradlew if unix or gradlew.bat if windows
        src_path
            .join(if cfg!(target_os = "windows") {
                "gradlew.bat"
            } else {
                "gradlew"
            })
            .to_str()
            .expect("Could not get the gradle path as string."),
        &["build", "--project-dir", src_path.to_str().unwrap()],
    );
    use_handling(
        &src_path
            .join("rest-webserver/target/release")
            .join(if cfg!(target_os = "windows") {
                "webserver.exe"
            } else {
                "webserver"
            }),
        &compiled_path.join("webserver.jar"),
        |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_file(src, dest)
        },
    );

    if let Some(module) = module {
        let module = module.trim().to_lowercase();
        let path = src_path.join("arcade").join(&module);
        if !path.exists() {
            panic!("The arcade module {} does not exist.", module);
        }
        let is_waterfall = WATERFALL_MODULES.contains(&module.as_str());
        if is_waterfall {
            panic!("Waterfall is not yet supported.");
        }
        let symlinked_plugins_directory = src_path.join("run").join("plugins");
        let dependencies_path = src_path.join("arcade").join(&module).join("DEPENDENCIES");
        if dependencies_path.exists() {
            let content = fs::read_to_string(dependencies_path)
                .expect("Failed to read the dependencies file.");
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
}
