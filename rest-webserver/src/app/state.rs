use std::sync::Arc;

use bb8_postgres::{PostgresConnectionManager, bb8::Pool};
use tokio_postgres::NoTls;

use hexalite_common::settings::HexaliteSettings;
use crate::bootstrap::{redis::RedisConnection, rabbitmq::RabbitMQService};

pub type WebServerStateRaw = std::sync::Arc<WebServerStateData>;
pub type WebServerState = actix_web::web::Data<WebServerStateRaw>;

pub type SqlPool = Pool<PostgresConnectionManager<NoTls>>;

#[derive(Clone)]
pub struct WebServerStateData {
    pub postgres: SqlPool,
    pub redis: RedisConnection,
    pub rabbitmq: RabbitMQService,
    pub settings: Arc<HexaliteSettings>,
}
