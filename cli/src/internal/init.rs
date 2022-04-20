use std::fs;
use std::io::Error;
use std::io::ErrorKind;
use std::path::Path;
use std::path::PathBuf;

lazy_static::lazy_static! {
    static ref PATH: PathBuf = {
        home::home_dir()
            .expect("Failed to get the home directory.")
            .to_owned()
            .join(".hexalite")
    };
    static ref FILES: Vec<&'static str> = vec![".env", "resource-pack", "run"];
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

fn use_handling<F>(src_path: &PathBuf, path: &str, func: F)
where
    F: FnOnce(PathBuf, &Path) -> Result<(), Error>,
{
    let src = src_path.join(path);
    let dest = &*PATH.join(path);
    func(src, dest).unwrap_or_else(handle_error);
}

pub fn init(src_path: PathBuf) {
    if let Err(err) = fs::create_dir_all(&*PATH) {
        handle_error(err);
    }
    let src_path = fs::canonicalize(src_path).expect("Failed to get the canonical source path.");
    for file in &*FILES {
        use_handling(&src_path, file, |src, dest| {
            println!(
                "Creating symbolic link {} to {}",
                src.to_str().unwrap(),
                dest.to_str().unwrap()
            );
            symlink::symlink_auto(src, dest)
        });
    }
}
