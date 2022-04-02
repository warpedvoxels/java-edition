use std::sync::Arc;

use crate::settings::WebserverSettings;
use sea_orm::{Database, DatabaseConnection};

pub async fn build(settings: &WebserverSettings) -> Arc<DatabaseConnection> {
    let url = settings.services.database.url();
    Arc::new(Database::connect(&url).await.expect("Failed to connect to database."))
}
