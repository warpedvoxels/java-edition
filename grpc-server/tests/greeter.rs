use anyhow::{Context, Result};
use grpc_server::definition::protocol::{greeter::GreeterClient, *};
use std::str::FromStr;
use tonic::transport::Uri;

mod common;

#[tokio::test]
async fn greet_test() -> Result<()> {
    let settings = common::common()?;

    let uri = format!(
        "http://{}:{}",
        settings.grpc.root.ip, settings.grpc.root.port
    );
    let uri = Uri::from_str(&uri).unwrap();

    let mut client = GreeterClient::connect(uri)
        .await
        .context("Failed to connect to the server. Make sure it is running.")?;

    log::info!(
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
