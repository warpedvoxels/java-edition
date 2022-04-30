use std::{
    fs,
    net::{Ipv4Addr, SocketAddr},
    path::{PathBuf},
};

use crate::io::*;

use chrono::Duration;
use serde::{Deserialize, Serialize};
use serde_with::serde_as;
use serde_with::DurationSeconds;

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct HexaliteSettings {
    #[serde(default)]
    pub webserver: WebServerSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct WebServerSettings {
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
    pub identity: WebServerIdentityServiceSettings,
    pub redis: WebServerRedisServiceSettings,
}

#[serde_as]
#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerIdentityServiceSettings {
    pub secret_key: String,
    #[serde_as(as = "DurationSeconds<i64>")]
    #[serde(rename = "expiration_in_seconds")]
    pub expiration: Duration,
    pub is_secure: bool,
    pub cookie_name: String
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerRedisServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerDatabaseServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
    pub user: String,
    pub password: String,
    pub database: String,
    pub pool: WebServerDatabasePoolServiceSettings
}

#[serde_as]
#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerDatabasePoolServiceSettings {
     /// The maximum number of connections allowed.
     pub max_size: u32,
     /// The minimum idle connection count the pool will attempt to maintain.
     pub min_idle: Option<u32>,
     /// The maximum lifetime, if any, that a connection is allowed.
     #[serde_as(as = "Option<DurationSeconds<i64>>")]
     #[serde(rename = "max_lifetime_in_seconds")]
     pub max_lifetime: Option<Duration>,
     /// The duration, if any, after which idle_connections in excess of `min_idle` are closed.
     #[serde_as(as = "Option<DurationSeconds<i64>>")]
     #[serde(rename = "idle_timeout_in_seconds")]
     pub idle_timeout: Option<Duration>,
     /// The duration to wait to start a connection before giving up.
     #[serde_as(as = "DurationSeconds<i64>")]
     #[serde(rename = "connection_timeout_in_seconds")]
     pub connection_timeout: Duration,
     /// The time interval used to wake up and reap connections.
     #[serde_as(as = "DurationSeconds<i64>")]
     #[serde(rename = "reaper_rate_in_seconds")]
     pub reaper_rate: Duration,
}

impl Default for WebServerDatabasePoolServiceSettings {
    fn default() -> Self {
        Self {
            max_size: 10,
            min_idle: None,
            max_lifetime: Some(Duration::minutes(30)),
            idle_timeout: Some(Duration::minutes(10)),
            connection_timeout: Duration::seconds(30),
            reaper_rate: Duration::seconds(30),
        }
    }
}

impl Default for WebServerRootSettings {
    fn default() -> Self {
        Self {
            ip: Ipv4Addr::new(127, 0, 0, 1),
            port: 8080,
        }
    }
}

impl Default for WebServerDatabaseServiceSettings {
    fn default() -> Self {
        Self {
            host: Ipv4Addr::LOCALHOST,
            port: 5432,
            user: String::from("johndoe"),
            password: String::from("mysecretpassword"),
            database: String::from("hexalite"),
            pool: WebServerDatabasePoolServiceSettings::default(),
        }
    }
}

impl Default for WebServerIdentityServiceSettings {
    fn default() -> Self {
        Self {
            secret_key: String::from("mysecretkey"),
            expiration: Duration::minutes(1),
            is_secure: false,
            cookie_name: String::from("sid"),
        }
    }
}

impl Default for WebServerRedisServiceSettings {
    fn default() -> Self {
        Self {
            host: Ipv4Addr::LOCALHOST,
            port: 6379,
        }
    }
}

impl WebServerRedisServiceSettings {
    pub fn url(&self) -> String {
        format!(
            "redis://{}:{}",
            self.host,
            self.port
        )
    }
}

impl WebServerDatabaseServiceSettings {
    pub fn url(&self) -> String {
        format!("postgresql://{}:{}/{}?user={}&password={}", self.host, self.port, self.database, self.user, self.password)
    }
}

impl Writer<()> for HexaliteSettings {
    fn write(&self, _: &()) -> Result<(), &str> {
        let path = path();
        if !path.exists() && fs::create_dir_all(path.parent().unwrap()).is_err() {
            return Err("Failed to create the settings directories.");
        }
        let toml = toml::to_string_pretty(self).expect("Failed to serialize the settings.");
        if fs::write(path, toml).is_err() {
            return Err("Failed to write the settings file.");
        };
        Ok(())
    }
}

impl Reader<HexaliteSettings, ()> for HexaliteSettings {
    fn read(_: &()) -> Result<HexaliteSettings, &str> {
        let path = path();
        if !path.exists() {
            let default = HexaliteSettings::default();
            if default.write(&()).is_err() {
                return Err("Failed to write the default settings.");
            }
        }
        let content = fs::read_to_string(path).expect("Failed to read the settings file.");
        let settings = toml::from_str(&content).expect("Failed to deserialize the settings.");
        Ok(settings)
    }
}

impl WebServerSettings {
    pub fn ip(&self) -> SocketAddr {
        SocketAddr::new(self.root.ip.into(), self.root.port)
    }
}

pub fn read() -> Result<HexaliteSettings, &'static str> {
    HexaliteSettings::read(&())
}

pub fn path() -> PathBuf {
    hexalite_common::dirs::get_hexalite_dir_path().join("settings.toml")
}
