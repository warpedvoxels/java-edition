use anyhow::{Context, Result};

use grpc_server::{bootstrap::*, routing::greeter::Greeter};
use tonic::transport::Server;

#[tokio::main]
async fn main() -> Result<()> {
    logging::init().unwrap();
    let settings = settings::init().unwrap();

    Server::builder()
        .add_service(Greeter::service())
        .serve(settings.grpc.ip())
        .await
        .context("Failed to serve the gRPC server.")
}