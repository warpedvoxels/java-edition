use std::path::PathBuf;

use prost_build::Config;

fn main() {
    let working_directory = std::env::current_dir().unwrap().join("../").canonicalize().unwrap();

    let mut files: Vec<PathBuf> = Vec::new();
    for receiver in ["", "entity/", "protocol/", "rest/"] {
        let path = format!("../definitions/{}*.proto", receiver);
        if let Ok(input) = glob::glob(&path) {
            let input = input.filter_map(|value| value.unwrap().canonicalize().ok());
            files.extend(input);
        }
    }
    println!("Files: {}", files.len());

    let mut config = Config::new();
    config.type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]");
    config.type_attribute(".", "#[serde(rename_all = \"snake_case\")]");
    config.out_dir("src/definitions");
    config
        .compile_protos(&files, &[working_directory])
        .unwrap();
}
