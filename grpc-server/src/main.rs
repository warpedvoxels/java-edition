use anyhow::{Context, Result};

use grpc_server::{bootstrap::*, routing::*};
use tonic::transport::Server;

#[tokio::main]
async fn main() -> Result<()> {
    logging::init().unwrap();
    let settings = settings::init()?;
    let _postgres = postgres::init(&settings).await?;

    Server::builder()
        .add_service(Greeter::service())
        .add_service(PlayerService::service())
        .serve(settings.grpc.ip())
        .await
        .context("Failed to serve the gRPC server.")
}
