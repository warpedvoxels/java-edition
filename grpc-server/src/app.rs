use hexalite_common::settings::HexaliteSettings;

use crate::bootstrap::postgres::SqlPool;

#[derive(Debug, Clone)]
pub struct HexaliteGrpcServer {
    pub settings: HexaliteSettings,
    pub sql: SqlPool,
}

impl HexaliteGrpcServer {
    pub fn new(settings: HexaliteSettings, sql: SqlPool) -> Self {
        Self { settings, sql }
    }
}
