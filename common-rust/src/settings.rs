use std::{
    fs,
    net::{Ipv4Addr, SocketAddr},
    path::PathBuf,
};

use anyhow::{Context, Result};
use chrono::Duration;
use serde::{Deserialize, Serialize};
use serde_with::serde_as;
use serde_with::DurationSeconds;
use std::fmt::Write;
use urlencoding::encode as url;

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct HexaliteSettings {
    #[serde(default)]
    pub grpc: GrpcSettings,
    #[serde(default)]
    pub webserver: WebServerSettings,
    #[serde(default)]
    pub discord: DiscordSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct WebServerSettings {
    #[serde(default)]
    pub root: WebServerRootSettings,
    #[serde(default)]
    pub grpc_client: GrpcRootSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct WebServerRootSettings {
    pub ip: Ipv4Addr,
    pub port: u16,
    pub ssl: bool,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct GrpcSettings {
    #[serde(default)]
    pub root: GrpcRootSettings,
    #[serde(default)]
    pub services: GrpcServicesSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcRootSettings {
    pub ip: Ipv4Addr,
    pub port: u16,
    pub ssl: bool,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct DiscordSettings {
    pub token: String,
    pub id: u64,
    pub public_key: String,
    pub secret: String,
    pub guild_id: Option<Vec<u64>>,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct GrpcServicesSettings {
    #[serde(default)]
    pub postgres: GrpcPostgresServiceSettings,
    #[serde(default)]
    pub identity: GrpcIdentityServiceSettings,
    #[serde(default)]
    pub rabbitmq: GrpcRabbitMQServiceSettings,
    #[serde(default)]
    pub redis: GrpcRedisServiceSettings,
}

#[serde_as]
#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcIdentityServiceSettings {
    pub secret_key: String,
    #[serde_as(as = "DurationSeconds<i64>")]
    #[serde(rename = "expiration_in_seconds")]
    pub expiration: Duration,
    pub is_secure: bool,
    pub cookie_name: String,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcRedisServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
    pub password: String,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcRabbitMQServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
    pub username: String,
    pub password: String,
}

#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcPostgresServiceSettings {
    pub host: Ipv4Addr,
    pub port: u16,
    pub username: String,
    pub password: Option<String>,
    pub database: String,
    pub schema: String,
    pub ssl: GrpcPostgresServiceSslSettings,
    pub pool: GrpcPostgresServicePoolSettings,
}

#[derive(Clone, Serialize, Deserialize, Debug, Default)]
pub struct GrpcPostgresServiceSslSettings {
    pub enable: bool,
    pub cert_path: Option<PathBuf>,
    pub identity_path: Option<PathBuf>,
    pub password: Option<String>,
}

#[serde_as]
#[derive(Clone, Serialize, Deserialize, Debug)]
pub struct GrpcPostgresServicePoolSettings {
    /// The maximum number of connections allowed.
    pub max_connections: u32,
    /// The maximum lifetime, if any, that a connection is allowed.
    #[serde_as(as = "DurationSeconds<i64>")]
    #[serde(rename = "max_lifetime_in_seconds")]
    pub max_lifetime: Duration,
    /// The duration, if any, after which idle_connections in excess of `min_idle` are closed.
    #[serde_as(as = "DurationSeconds<i64>")]
    #[serde(rename = "idle_timeout_in_seconds")]
    pub idle_timeout: Duration,
    /// The duration to wait to start a connection before giving up.
    #[serde_as(as = "DurationSeconds<i64>")]
    #[serde(rename = "connection_timeout_in_seconds")]
    pub connection_timeout: Duration,
}

impl Default for WebServerRootSettings {
    fn default() -> Self {
        Self {
            ip: Ipv4Addr::LOCALHOST,
            port: 8080,
            ssl: false,
        }
    }
}

impl Default for GrpcPostgresServicePoolSettings {
    fn default() -> Self {
        Self {
            max_connections: 10,
            max_lifetime: Duration::minutes(30),
            idle_timeout: Duration::minutes(10),
            connection_timeout: Duration::seconds(30),
        }
    }
}

impl Default for GrpcRootSettings {
    fn default() -> Self {
        Self {
            ip: Ipv4Addr::LOCALHOST,
            port: 50051,
            ssl: false,
        }
    }
}

impl Default for GrpcPostgresServiceSettings {
    fn default() -> Self {
        Self {
            host: Ipv4Addr::LOCALHOST,
            port: 5432,
            username: String::from("johndoe"),
            password: Some(String::from("mysecretpassword")),
            database: String::from("hexalite"),
            schema: String::from("public"),
            ssl: GrpcPostgresServiceSslSettings::default(),
            pool: GrpcPostgresServicePoolSettings::default(),
        }
    }
}

impl Default for GrpcIdentityServiceSettings {
    fn default() -> Self {
        Self {
            secret_key: String::from("mysecretkey"),
            expiration: Duration::minutes(1),
            is_secure: false,
            cookie_name: String::from("sid"),
        }
    }
}

impl Default for GrpcRedisServiceSettings {
    fn default() -> Self {
        Self {
            host: Ipv4Addr::LOCALHOST,
            port: 6379,
            password: String::from("mysecretpassword"),
        }
    }
}

impl Default for GrpcRabbitMQServiceSettings {
    fn default() -> Self {
        Self {
            host: Ipv4Addr::LOCALHOST,
            port: 5672,
            username: String::from("johndoe"),
            password: String::from("mysecretpassword"),
        }
    }
}

impl Default for DiscordSettings {
    fn default() -> Self {
        Self {
            token: "token-here!@*!*&#".to_string(),
            id: 950527109774843904,
            public_key: "public-key-here-xd2352384523w".to_string(),
            secret: "secret-here-asasdasdsadscsa".to_string(),
            guild_id: Some(vec![908438033613848596])
        }
    }
}

impl ToString for GrpcRabbitMQServiceSettings {
    fn to_string(&self) -> String {
        format!(
            "amqp://{}:{}@{}:{}",
            self.username, self.password, self.host, self.port
        )
    }
}

impl ToString for GrpcRedisServiceSettings {
    fn to_string(&self) -> String {
        format!("redis://:{}@{}:{}", self.password, self.host, self.port)
    }
}

impl ToString for GrpcPostgresServiceSettings {
    fn to_string(&self) -> String {
        let mut buf = format!("postgresql://{}", url(&self.username));
        if let Some(password) = &self.password {
            write!(buf, ":{}", url(password)).unwrap();
        }
        write!(
            buf,
            "@{}:{}/{}?schema={}",
            url(&self.host.to_string()),
            self.port,
            self.database,
            self.schema
        )
        .unwrap();
        if self.ssl.enable {
            buf.push_str("&sslmode=require&sslaccept=strict");
            if let Some(cert) = &self.ssl.cert_path {
                write!(buf, "&sslcert={}", url(cert.to_str().unwrap())).unwrap();
            }
            if let Some(identity) = &self.ssl.identity_path {
                write!(buf, "&sslidentity={}", url(identity.to_str().unwrap())).unwrap();
            }
            if let Some(password) = &self.ssl.password {
                write!(buf, "&sslpassword={}", url(password)).unwrap();
            }
        }
        write!(
            buf,
            "&connection_limit={}&connect_timeout={}&pool_timeout={}&socket_timeout={}",
            self.pool.max_connections,
            self.pool.connection_timeout.num_seconds(),
            self.pool.idle_timeout.num_seconds(),
            self.pool.max_lifetime.num_seconds()
        )
        .unwrap();
        buf
    }
}

impl HexaliteSettings {
    pub fn write(&self) -> Result<()> {
        let path = path();
        if !path.exists() && fs::create_dir_all(path.parent().unwrap()).is_err() {
            anyhow::bail!("Failed to create the settings directories.");
        }
        let toml = toml::to_string_pretty(self).expect("Failed to serialize the settings.");
        if fs::write(path, toml).is_err() {
            anyhow::bail!("Failed to write the settings file.");
        };
        Ok(())
    }
}

impl HexaliteSettings {
    pub fn read() -> Result<HexaliteSettings> {
        let path = path();
        if !path.exists() {
            let default = HexaliteSettings::default();
            if default.write().is_err() {
                anyhow::bail!("Failed to write the default settings.");
            }
        }
        let content = fs::read_to_string(path).context("Failed to read the settings file.")?;
        let settings = toml::from_str(&content).context("Failed to deserialize the settings.")?;
        Ok(settings)
    }
}

impl GrpcSettings {
    pub fn ip(&self) -> SocketAddr {
        SocketAddr::new(self.root.ip.into(), self.root.port)
    }
}

pub fn read() -> Result<HexaliteSettings> {
    HexaliteSettings::read()
}

pub fn path() -> PathBuf {
    crate::dirs::get_hexalite_dir_path().join("settings.toml")
}
