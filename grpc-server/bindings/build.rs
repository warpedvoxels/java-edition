use std::env;

fn main() {
    println!("cargo:rerun-if-changed=build.rs");
    println!("cargo:rerun-if-changed=src/*.rs");
    let crate_dir = env::var("CARGO_MANIFEST_DIR").unwrap();

    let working_directory = env::current_dir()
        .unwrap()
        .join("../../")
        .canonicalize()
        .unwrap();
    let target = working_directory.join("target/release/client.h");

    cbindgen::generate(&crate_dir)
        .unwrap()
        .write_to_file(target);
}
