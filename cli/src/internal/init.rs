use std::fs;
use std::path::PathBuf;

use crate::internal::{handle_dir_error, use_handling_auto, HEXALITE};

use super::use_handling;

lazy_static::lazy_static! {
    static ref FILES: Vec<&'static str> = vec![".env", "resource-pack", "run"];
}

pub fn init(src_path: PathBuf) {
    if let Err(err) = fs::create_dir_all(&*HEXALITE) {
        handle_dir_error(&*HEXALITE, &*HEXALITE, err);
    }
    let src_path = fs::canonicalize(src_path).expect("Failed to get the canonical source path.");
    use_handling(&src_path, &*HEXALITE.join("dev"), |src, dest| {
        symlink::symlink_dir(src, dest)
    });
    for file in &*FILES {
        use_handling_auto(&src_path, file, |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_auto(src, dest)
        });
    }
}
