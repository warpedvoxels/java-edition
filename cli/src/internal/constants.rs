use std::path::PathBuf;

lazy_static::lazy_static! {
    pub static ref HEXALITE: PathBuf = {
        home::home_dir()
            .expect("Failed to get the home directory.")
            .join(".hexalite")
    };
}
