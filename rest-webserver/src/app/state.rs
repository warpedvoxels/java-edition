use std::sync::Arc;

use bb8_postgres::{PostgresConnectionManager, bb8::Pool};
use tokio_postgres::NoTls;

use crate::settings::HexaliteSettings;

pub type WebServerStateRaw = std::sync::Arc<WebServerStateData>;
pub type WebServerState = actix_web::web::Data<WebServerStateRaw>;

pub type SqlPool = Pool<PostgresConnectionManager<NoTls>>;

#[derive(Debug, Clone)]
pub struct WebServerStateData {
    pub pool: SqlPool,
    pub settings: Arc<HexaliteSettings>,
}
