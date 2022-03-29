use std::{fs::{self, File}, path::Path, process::exit};

use serde::{Serialize, Deserialize};

const CONFIG_PATH: &'static str = "/home/hexalite/webserver.toml";

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct Config {
    pub ip: String,
    pub port: u16,
}

impl Default for Config {
    fn default() -> Self {
        Config {
            ip: String::from("127.0.0.1"),
            port: 8080,
        }
    }
}

pub fn load() -> Config {
    let config_path = Path::new(CONFIG_PATH);
    if !config_path.exists() {
        log::error!("config file not found, creating one and aborting process");
        fs::create_dir_all(config_path.parent().unwrap()).expect("couldn't create config file path");
        File::create(config_path).expect("failed to create config file");
        fs::write(config_path, toml::to_string_pretty(&Config::default()).expect("error while trying to create new config")).expect("error while trying to generate default config specs");
        exit(-1);
    }
    let config: Config = toml::from_str(fs::read_to_string(config_path).expect("error while reading the config file").as_str()).expect("failed to parse config");
    config
}