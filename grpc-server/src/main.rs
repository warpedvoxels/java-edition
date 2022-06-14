use anyhow::{Context, Result};

use grpc_server::{bootstrap::*, app::*, routing::*};
use tonic::transport::Server;

#[tokio::main]
async fn main() -> Result<()> {
    if cfg!(feature = "client") {
        panic!("client feature is not supported for initializing");
    }
    logging::init().unwrap();
    let settings = settings::init()?;
    let postgres = postgres::init(&settings).await?;
    let app = HexaliteGrpcServer::new(settings, postgres);
    
    Server::builder()
        .add_service(Greeter::service())
        .add_service(PlayerService::service())
        .serve(app.settings.grpc.ip())
        .await
        .context("Failed to serve the gRPC server.")
}
