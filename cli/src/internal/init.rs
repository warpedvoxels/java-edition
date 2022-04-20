use std::fs;
use std::io::Error;
use std::io::ErrorKind;
use std::path::PathBuf;

lazy_static::lazy_static! {
    static ref PATH: PathBuf = {
        home::home_dir()
            .expect("Failed to get the home directory.")
            .to_owned()
            .join(".hexalite")
    };
    static ref DIRS: Vec<&'static str> = vec!["resource-pack",  "run"];
    static ref FILES: Vec<&'static str> = vec![".env"];
}

fn handle_error(err: Error) {
    if err.kind() != ErrorKind::AlreadyExists {
        panic!(
            "Failed to create the directory {}: {}",
            &*PATH.to_str().unwrap(),
            err
        );
    }
}

pub fn init(src_path: PathBuf) {
    if let Err(err) = fs::create_dir_all(&*PATH) {
        handle_error(err);
    }
    let src_path = fs::canonicalize(src_path).expect("Failed to get the canonical source path.");
    for file in &*FILES {
        let src = src_path.join(file);
        let dest = &*PATH.join(file);
        if let Err(err) = symlink::symlink_file(&src, &dest) {
            handle_error(err);
        }
        println!(
            "Created symbolic link {} to {}",
            src.to_str().unwrap(),
            dest.to_str().unwrap()
        );
    }
    for dir in &*DIRS {
        let src = src_path.join(dir);
        let dest = &*PATH.join(dir);
        if let Err(err) = symlink::symlink_dir(&src, &dest) {
            handle_error(err);
        }
        println!(
            "Created symbolic link {} to {}",
            src.to_str().unwrap(),
            dest.to_str().unwrap()
        );
    }
}
