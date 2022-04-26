extern crate glob;
extern crate protoc_bin_vendored;
extern crate protoc_rust;

fn main() {
    let bin = protoc_bin_vendored::protoc_bin_path().unwrap();
    let hexalite = home::home_dir()
            .expect("Failed to get the home directory.")
            .join(".hexalite")
            .canonicalize()
            .unwrap();
    let dir = hexalite.join("dev").canonicalize().unwrap();
    let dir = dir.join("definitions");

    let input = glob::glob("../definitions/*.proto").expect("Failed to find definitions.");
    let input: Vec<_> = input
        .filter_map(|value| value.unwrap().canonicalize().ok())
        .collect();

    protoc_rust::Codegen::new()
        .protoc_path(bin)
        .out_dir("src/definitions")
        .inputs(input)
        .include(dir)
        .run()
        .expect("Failed to generate protobuf bindings.");
}
