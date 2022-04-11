use std::str::FromStr;

use actix_web::{get, post, web, Either, HttpResponse, Responder};
use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::{
    app::WebserverState,
    entity::{Entity, Player},
};

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq)]
pub struct RestV1Player {
    pub uuid: Uuid,
    pub hexes: u32,
    pub last_username: String,
    pub last_seen: DateTime<Utc>,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

#[derive(Debug, Clone, Serialize, Deserialize, PartialEq, Eq, Default)]
pub struct RestV1PlayerCreationData {
    pub uuid: Uuid,
    pub last_username: Option<String>,
}

impl RestV1Player {
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

#[get("/{id}")]
pub async fn find(id: web::Path<String>, state: WebserverState) -> impl Responder {
    match id.as_str() {
        "all" => {
            let players: Vec<RestV1Player> = Player::find_all(&state)
                .await
                .iter()
                .map(RestV1Player::from)
                .collect();
            HttpResponse::Ok().json(players)
        }
        id => {
            let either = if let Ok(uuid) = Uuid::from_str(id) {
                Either::Left(uuid)
            } else {
                Either::Right(id.to_owned())
            };
            let player = Player::find(&state, either).await;
            match player {
                Some(p) => HttpResponse::Ok().json(RestV1Player::from(&p)),
                None => HttpResponse::NotFound().finish(),
            }
        }
    }
}

#[post("/")]
pub async fn create(
    player: web::Json<RestV1Player>,
    state: WebserverState,
) -> impl Responder {
    let player = Player {
        uuid: player.uuid,
        last_username: player.last_username.clone(),
        ..Default::default()
    };
    let result = player.create(&state).await;
    if result.is_err() {
        log::error!("{:?}", result.err().unwrap());
        HttpResponse::InternalServerError().body("Failed to create a new player.")
    } else {
        HttpResponse::Ok().json(RestV1Player::from(&player))
    }
}
