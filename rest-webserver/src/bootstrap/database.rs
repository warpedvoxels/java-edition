use std::{time::Duration};

use crate::{app::{SqlPool, PoolOptions}, settings::WebserverSettings};
use sqlx::PgPool;

pub async fn build(settings: &WebserverSettings) -> Result<SqlPool, String> {
    let url = settings.services.database.url();

    let options = PoolOptions::new()
        .connect_timeout(Duration::from_nanos(30));
        
    let _pool = options.connect(&url);
    let connection = PgPool::connect(url.as_str()).await;

    if let Err(e) = connection {
        return Err(format!("Could not connect to database: {}", e));
    }
    log::info!("Connected to the database succesfully!");
    Ok(connection.unwrap())
}
