use std::sync::Arc;

use sea_orm::DatabaseConnection;

use crate::settings::WebserverSettings;

#[derive(Debug, Clone)]
pub struct WebserverState {
    pub database: Arc<DatabaseConnection>,
    pub settings: Arc<WebserverSettings>,
}
