use anyhow::{Context, Result};
use grpc_server::definition::{protocol::{player::PlayerClient, *}, datatype::id::Data as Id};
use uuid::Uuid;
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

    let mut client = PlayerClient::connect(uri)
        .await
        .context("Failed to connect to the server. Make sure it is running.")?;

    log::info!(
        "{:?}",
        client
            .retrieve_data(PlayerDataRequest { id: Id::Uuid(Uuid::new_v4()).into() })
            .await
            .context("Failed to request data.")?
    );

    Ok(())
}
