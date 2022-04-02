use std::sync::Arc;

use sea_orm::DatabaseConnection;

#[derive(Debug, Clone)]
pub struct WebserverState {
    pub connection: Arc<DatabaseConnection>
}