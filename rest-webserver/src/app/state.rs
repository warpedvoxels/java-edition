use std::sync::Arc;

use bb8_postgres::{bb8::Pool, PostgresConnectionManager};
use tokio_postgres::NoTls;

use crate::bootstrap::{rabbitmq::RabbitMQService, redis::RedisConnection};
use hexalite_common::settings::HexaliteSettings;

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
