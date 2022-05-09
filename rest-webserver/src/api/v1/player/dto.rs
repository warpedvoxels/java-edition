use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::entity::Player;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct ReturnedPlayerCreationData {
    pub uuid: Uuid,
    pub last_username: String,
}

pub type ReturnedPlayerCreation = actix_web::web::Json<ReturnedPlayerCreationData>;

impl From<ReturnedPlayerCreation> for Player {
    fn from(data: ReturnedPlayerCreation) -> Player {
        Player {
            uuid: data.uuid,
            last_username: data.last_username.clone(),
            ..Default::default()
        }
    }
}
