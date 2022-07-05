use std::sync::Arc;

use anyhow::{Context, Result};

use grpc_server::{app::*, bootstrap::*, routing::*};
use tonic::transport::Server;

#[tokio::main]
async fn main() -> Result<()> {
    if cfg!(feature = "client") {
        panic!("client feature is not supported for initializing");
    }
    logging::init().unwrap();
    let settings = settings::init()?;
    let postgres = postgres::init(&settings).await?;
    let app = Arc::new(HexaliteGrpcServer::new(settings, postgres));

    Server::builder()
        .add_service(Greeter::service())
        .add_service(PlayerService::service(app.clone()))
        .serve(app.settings.grpc.ip())
        .await
        .context("Failed to serve the gRPC server.")
}
