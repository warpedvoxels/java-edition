use anyhow::{Context, Result};
use chrono::{Utc, Duration};
use jsonwebtoken::{decode, encode, DecodingKey, EncodingKey, Validation, TokenData, Header};
use uuid::Uuid;

use crate::{app::WebServerState, definitions::rest::Authorization, settings::WebServerSettings};

impl Authorization {
    pub fn new(uuid: Option<Uuid>, is_internal: bool, settings: &WebServerSettings) -> Self {
        Self {
            sub: uuid,
            exp: Utc::now() + settings.services.identity.expiration,
            is_internal,
        }
    }

    pub fn new_internal() -> Self {
        Self {
            sub: None,
            exp: Utc::now() + Duration::minutes(5),
            is_internal: true,
        }
    }

    pub fn decode(token: &str, state: &WebServerState) -> Result<TokenData<Self>> {
        let decoding_key = DecodingKey::from_secret(state.settings.webserver.services.identity.secret_key.as_ref());
        decode::<Authorization>(token, &decoding_key, &Validation::new(jsonwebtoken::Algorithm::HS512))
            .context("Couldn't decode the provided JWT authorization.")
    }

    pub fn encode(&self, settings: &WebServerSettings) -> Result<String> {
        let header = Header::new(jsonwebtoken::Algorithm::HS512);
        let encoding_key = EncodingKey::from_secret(settings.services.identity.secret_key.as_ref());
        encode::<Authorization>(&header, self, &encoding_key)
            .context("Couldn't encode the provided JWT authorization.")
    }
}
