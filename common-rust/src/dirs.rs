use std::path::PathBuf;

use anyhow::{Result, Context};

lazy_static::lazy_static! {
    pub(crate) static ref HEXALITE: PathBuf = {
        home::home_dir()
            .expect("Failed to get the home directory.")
            .join(".hexalite")
    };
}

pub fn get_hexalite_dir_path() -> PathBuf {
    HEXALITE.clone()
}

pub fn get_source_path() -> Result<PathBuf> {
    HEXALITE.join("dev").canonicalize().context("Failed to get the canonical source path.")
}
