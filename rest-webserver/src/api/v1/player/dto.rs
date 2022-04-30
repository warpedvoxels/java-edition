use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::entity::Player;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct RestPlayerCreationData {
    pub uuid: Uuid,
    pub last_username: String,
}

pub type RestPlayerCreation = actix_web::web::Json<RestPlayerCreationData>;

impl From<RestPlayerCreation> for Player {
    fn from(data: RestPlayerCreation) -> Player {
        Player {
            uuid: data.uuid,
            last_username: data.last_username.clone(),
            ..Default::default()
        }
    }
}
