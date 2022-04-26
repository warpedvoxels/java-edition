use std::fs;
use std::path::PathBuf;

use hexalite_common::dirs::get_hexalite_dir_path;

use crate::internal::{handle_dir_error, use_handling_auto};

use super::use_handling;

lazy_static::lazy_static! {
    static ref FILES: Vec<&'static str> = vec![".env", "resource-pack", "run"];
}

pub fn init(src_path: PathBuf) {
    let hexalite = get_hexalite_dir_path();
    if let Err(err) = fs::create_dir_all(&hexalite) {
        handle_dir_error(&hexalite, &hexalite, err);
    }
    let src_path = fs::canonicalize(src_path).expect("Failed to get the canonical source path.");
    use_handling(&src_path, &hexalite.join("dev"), |src, dest| {
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
