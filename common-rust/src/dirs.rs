use std::fs;
use std::path::PathBuf;

lazy_static::lazy_static! {
    pub(crate) static ref HEXALITE: PathBuf = {
        home::home_dir()
            .expect("Failed to get the home directory.")
            .join(".hexalite")
    };
}

pub fn get_hexalite_dir_path() -> PathBuf {
    HEXALITE.clone().to_path_buf()
}

pub fn get_source_path() -> PathBuf {
    fs::canonicalize(&*HEXALITE.join("dev")).expect("Failed to get the canonical source path.")
}
