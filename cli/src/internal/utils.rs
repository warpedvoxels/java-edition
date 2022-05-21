use std::{
    io::{Error, ErrorKind},
    path::Path,
};

use anyhow::Result;

use hexalite_common::prelude::get_hexalite_dir_path;

#[macro_export]
macro_rules! file_from_src {
    ($path:expr) => {{
        let src = hexalite_common::dirs::get_source_path().unwrap();
        let file_name = $crate::internal::utils::file_name!($path);
        src.join("target/release").join(file_name)
    }};
}

#[macro_export]
macro_rules! compiled_file {
    ($path:expr) => {{
        let src = hexalite_common::dirs::get_hexalite_dir_path();
        let file_name = $crate::internal::utils::file_name!($path);
        src.join("compiled").join(file_name)
    }};
}

#[macro_export]
macro_rules! file_name {
    ($path:expr) => {
        if cfg!(target_os = "windows") {
            stringify!($path.exe)
        } else {
            $path
        }
    };
}

pub use {compiled_file, file_from_src, file_name};

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
