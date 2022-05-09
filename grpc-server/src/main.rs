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

#[tokio::test]
async fn greet_test() -> Result<()> {
    use grpc_server::definition::protocol::{greeter::GreeterClient, *};
    use std::str::FromStr;
    use tonic::transport::Uri;

    let settings = settings::init().unwrap();
    let uri = Uri::from_str(&format!(
        "http://{}:{}",
        settings.grpc.root.ip, settings.grpc.root.port
    ))
    .unwrap();
    println!("Initializing greeter test at {uri}");

    let mut client = GreeterClient::connect(uri).await?;

    println!(
        "{:?}",
        client
            .say_hello(HelloRequest {
                name: "John Doe".into(),
            })
            .await
            .context("Failed to request Hello.")?
    );

    Ok(())
}
