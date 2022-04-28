use std::path::PathBuf;

use prost_build::Config;

fn main() {
    println!("> Generating entities by ProtoBuf definitions...");

    let working_directory = std::env::current_dir()
        .unwrap()
        .join("../")
        .canonicalize()
        .unwrap();
    let working_directory = working_directory.join("definitions");

    let mut files: Vec<PathBuf> = Vec::new();
    if let Ok(input) = glob::glob("../definitions/**/*.proto") {
        let input = input.filter_map(|value| value.unwrap().canonicalize().ok());
        files.extend(input);
    }
    println!("Files: {}", files.len());

    Config::new()
        .type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]")
        .type_attribute(".", "#[serde(rename_all = \"snake_case\")]")
        .extern_path(".datatype.Uuid", "::uuid::Uuid")
        .out_dir("src/definitions")
        .compile_protos(&files, &[working_directory])
        .unwrap();
}
