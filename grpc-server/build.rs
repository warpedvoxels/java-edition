use std::path::PathBuf;

fn main() {
    println!("cargo:rerun-if-changed=../definitions");
    println!("cargo:rerun-if-changed=build.rs");

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

    tonic_build::configure()
        .build_client(false)
        .type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]")
        .type_attribute(".", "#[serde(rename_all = \"snake_case\")]")
        .type_attribute(".entity", "#[sea_query::enum_def(suffix = \"TypeDef\")]")
        .extern_path(
            ".google.protobuf.Timestamp",
            "::chrono::DateTime<::chrono::Utc>",
        )
        .extern_path(".datatype.Uuid", "::uuid::Uuid")
        .out_dir("src/definitions")
        .compile(&files, &[working_directory])
        .expect("Failed to compile the protocol buffer definitions");
}
