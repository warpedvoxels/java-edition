use std::sync::Arc;

use crate::settings::WebserverSettings;
use sqlx::{PgPool, Postgres, pool::PoolConnection};

sea_query::sea_query_driver_postgres!();

pub async fn build(settings: &WebserverSettings) -> Result<Arc<PoolConnection<Postgres>>, String> {
    let url = settings.services.database.url();
    let connection = PgPool::connect(url.as_str()).await;
    if let Err(e) = connection {
        return Err(format!("Could not connect to database: {}", e));
    }
    let pool = connection.unwrap().try_acquire();
    if pool.is_none() {
        return Err("No idle connections available for the database".to_string());
    }
    log::info!("Connected to the database succesfully!");
    Ok(Arc::new(pool.unwrap()))
}
