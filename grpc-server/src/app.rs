use hexalite_common::settings::HexaliteSettings;
use crate::prisma::PrismaClient;

#[derive(Debug)]
pub struct HexaliteGrpcServer {
    pub settings: HexaliteSettings,
    pub sql: PrismaClient,
}

impl HexaliteGrpcServer {
    pub fn new(settings: HexaliteSettings, sql: PrismaClient) -> Self {
        Self { settings, sql }
    }
}
