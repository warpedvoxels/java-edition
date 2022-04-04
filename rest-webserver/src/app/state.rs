use std::sync::Arc;

use sqlx::{pool::PoolConnection, Postgres};
use crate::settings::WebserverSettings;

#[derive(Debug, Clone)]
pub struct WebserverState {
    pub database: Arc<PoolConnection<Postgres>>,
    pub settings: Arc<WebserverSettings>,
}
