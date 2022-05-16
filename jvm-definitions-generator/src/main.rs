use std::path::PathBuf;

use jvm_definitions_generator::config::DefGeneratorConfig;

fn main() {
    let working_directory = hexalite_common::dirs::get_source_path().unwrap();
    let working_directory = working_directory.join("definitions");

    let mut files: Vec<PathBuf> = Vec::new();
    if let Ok(input) = glob::glob(working_directory.join("**/*.proto").to_str().unwrap()) {
        let input = input.filter_map(|value| value.unwrap().canonicalize().ok());
        files.extend(input);
    }
    println!("Files: {}", files.len());

    DefGeneratorConfig::default()
        .extern_path(".datatype.Uuid", "java.util.UUID")
        .extern_path(".datatype.Username", "String")
        .compile(&files, &[working_directory])
        .expect("Failed to compile the protocol buffer definitions");
}
