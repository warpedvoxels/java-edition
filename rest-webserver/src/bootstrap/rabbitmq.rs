use std::sync::Arc;

use anyhow::{Context, Result};
use lapin::{
    options::QueueDeclareOptions, types::FieldTable, Channel, Connection, ConnectionProperties,
};

use crate::definitions::protocol::CommunicationsKey;
use hexalite_common::settings::GrpcSettings;

#[derive(Clone)]
pub struct RabbitMQService {
    pub connection: Arc<Connection>,
    pub channel: Channel,
}

pub async fn build(settings: &GrpcSettings) -> Result<RabbitMQService> {
    let url = settings.services.rabbitmq.url();
    let connection = Connection::connect(&url, ConnectionProperties::default())
        .await
        .unwrap();

    let channel = connection
        .create_channel()
        .await
        .context("Failure to create the channel.")?;

    for key in CommunicationsKey::values() {
        channel
            .queue_declare(
                &key.to_string(),
                QueueDeclareOptions::default(),
                FieldTable::default(),
            )
            .await
            .context("Failed to the declare the queue.")?;
    }

    Ok(RabbitMQService {
        connection: Arc::new(connection),
        channel,
    })
}
