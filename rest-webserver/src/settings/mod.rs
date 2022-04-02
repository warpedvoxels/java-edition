use std::{
    fs,
    net::{Ipv4Addr, SocketAddr},
    path::Path,
};

use crate::io::*;
use lazy_static::lazy_static;
use serde::{Deserialize, Serialize};

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct WebserverSettings {
    #[serde(default)]
    pub root: WebServerRootSettings,
    #[serde(default)]
    pub services: WebServerServicesSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerRootSettings {
    pub ip: Ipv4Addr,
    pub port: u16,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct WebServerServicesSettings {
    pub database: WebServerDatabaseServiceSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerDatabaseServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
    pub user: String,
    pub password: String,
    pub database: String,
}

impl Default for WebServerRootSettings {
    fn default() -> Self {
        WebServerRootSettings {
            ip: Ipv4Addr::new(127, 0, 0, 1),
            port: 8080,
        }
    }
}

impl Default for WebServerDatabaseServiceSettings {
    fn default() -> Self {
        WebServerDatabaseServiceSettings {
            host: Ipv4Addr::new(127, 0, 0, 1),
            port: 5432,
            user: String::from("johndoe"),
            password: String::from("mysecretpassword"),
            database: String::from("hexalite"),
        }
    }
}

impl WebServerDatabaseServiceSettings {
    pub fn url(&self) -> String {
        format!("postgresql://{}:{}/{}?user={}&password={}", self.host, self.port, self.database, self.user, self.password)
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
            if default.write(&()).is_err() {
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
        SocketAddr::new(self.root.ip.into(), self.root.port)
    }
}

lazy_static! {
    static ref PATH: String = {
        let home = home::home_dir()
            .expect("Failed to get the home directory.")
            .to_str()
            .expect("Failed to get the home directory as string.")
            .to_owned();
        format!("{}/.hexalite/webserver.toml", home)
    };
}

pub fn read() -> Result<WebserverSettings, &'static str> {
    WebserverSettings::read(&())
}

pub fn path() -> &'static Path {
    Path::new(&*PATH)
}
