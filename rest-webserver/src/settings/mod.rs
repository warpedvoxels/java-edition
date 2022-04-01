use std::{
    fs,
    net::{Ipv4Addr, SocketAddr},
    path::Path,
    sync::{Mutex, MutexGuard},
};

use crate::io::*;
use lazy_static::lazy_static;
use serde::{Deserialize, Serialize};

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebserverSettings {
    pub ip: Ipv4Addr,
    pub port: u16,
}

impl Default for WebserverSettings {
    fn default() -> Self {
        WebserverSettings {
            ip: Ipv4Addr::new(127, 0, 0, 1),
            port: 8080,
        }
    }
}

impl Writer<()> for WebserverSettings {
    fn write(&self, _: &()) -> Result<(), &str> {
        let path = path();
        if !path.exists() {
            if let Err(_) = fs::create_dir_all(path.parent().unwrap()) {
                return Err("Failed to create the settings directories.");
            }
        }
        let toml = toml::to_string_pretty(self).expect("Failed to serialize the settings.");
        if let Err(_) = fs::write(path, toml) {
            return Err("Failed to write the settings file.");
        };
        Ok(())
    }
}

impl Reader<WebserverSettings, ()> for WebserverSettings {
    fn read(_: &()) -> Result<WebserverSettings, &str> {
        let path = path();
        if !path.exists() {
            let default = WebserverSettings::default();
            if let Err(_) = default.write(&()) {
                return Err("Failed to write the default settings.");
            }
        }
        let content = fs::read_to_string(path).expect("Failed to read the settings file.");
        let settings = toml::from_str(&content).expect("Failed to deserialize the settings.");
        Ok(settings)
    }
}

impl WebserverSettings {
    pub fn ip(&self) -> SocketAddr {
        SocketAddr::new(self.ip.into(), self.port)
    }
}

lazy_static! {
    pub static ref SETTINGS: Mutex<WebserverSettings> =
        Mutex::new(WebserverSettings::read(&()).expect("Failed to read the settings."));
    static ref PATH: String = {
        let home = home::home_dir().expect("Failed to get the home directory.")
            .to_str().expect("Failed to get the home directory as string.")
            .to_owned();
        format!("{}/.hexalite/webserver.toml", home)
    };
}

pub fn read() -> MutexGuard<'static, WebserverSettings> {
    SETTINGS
        .lock()
        .expect("Couldn't unlock the settings mutex.")
}

pub fn write() {
    read().write(&()).expect("Failed to write the settings.");
}

pub fn path() -> &'static Path {
    Path::new(&*PATH)
}
