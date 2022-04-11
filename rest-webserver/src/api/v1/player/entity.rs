use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::entity::Player;

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq)]
pub struct RestV1Player {
    pub uuid: Uuid,
    pub hexes: u32,
    pub last_username: String,
    pub last_seen: DateTime<Utc>,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

impl From<&Player> for RestV1Player {
    fn from(db: &Player) -> RestV1Player {
        RestV1Player {
            uuid: db.uuid,
            hexes: db.hexes,
            last_username: db.last_username.clone(),
            last_seen: db.last_seen,
            created_at: db.created_at,
            updated_at: db.updated_at,
        }
    }
}