use std::sync::Arc;

use crate::settings::WebserverSettings;

pub type SqlPool = sqlx::PgPool;
pub type SqlQueryResult = sqlx::postgres::PgQueryResult;

pub type PoolOptions = sqlx::postgres::PgPoolOptions;

pub type WebserverStateRaw = std::sync::Arc<WebserverStateData>;
pub type WebserverState = actix_web::web::Data<WebserverStateRaw>;

#[derive(Debug, Clone)]
pub struct WebserverStateData {
    pub pool: SqlPool,
    pub settings: Arc<WebserverSettings>,
}
