use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::entity::Player;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct RestV1PlayerCreationData {
    pub uuid: Uuid,
    pub last_username: String,
}

pub type RestV1PlayerCreation = actix_web::web::Json<RestV1PlayerCreationData>;

impl From<RestV1PlayerCreation> for Player {
    fn from(data: RestV1PlayerCreation) -> Player {
        Player {
            uuid: data.uuid,
            last_username: data.last_username.clone(),
            ..Default::default()
        }
    }
}