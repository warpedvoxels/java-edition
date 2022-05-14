use std::{
    io::{Error, ErrorKind},
    path::Path,
    process::Stdio,
};

use anyhow::{Context, Result};
use hexalite_common::prelude::get_hexalite_dir_path;
use xshell::Shell;

pub fn handle_dir_error(src: &Path, dest: &Path, err: Error) {
    if err.kind() != ErrorKind::AlreadyExists {
        panic!(
            "Failed to create {}: {} => {}",
            src.to_str().unwrap(),
            dest.to_str().unwrap(),
            err
        );
    }
}

pub fn use_handling<F>(src: &Path, dest: &Path, func: F)
where
    F: FnOnce(&Path, &Path) -> Result<(), Error>,
{
    func(src, dest).unwrap_or_else(|err| handle_dir_error(src, dest, err));
}

pub fn use_handling_auto<F>(src_path: &Path, path: &str, func: F)
where
    F: FnOnce(&Path, &Path) -> Result<(), Error>,
{
    let hexalite = get_hexalite_dir_path();
    let src = src_path.join(path);
    let dest = hexalite.join(path);
    use_handling(&src, &dest, func)
}
