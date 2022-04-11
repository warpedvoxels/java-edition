use std::str::FromStr;

use actix_web::{
    delete, get, post,
    web::{self, Query},
    Either, HttpResponse, Responder,
};
use uuid::Uuid;

use crate::{
    api::PageInfo,
    app::WebserverState,
    entity::{Entity, Player},
    util::try_either,
};

mod dto;
mod entity;
pub use dto::*;
pub use entity::*;

#[get("/")]
pub async fn find_all(pagination: Query<PageInfo>, state: WebserverState) -> impl Responder {
    let limit = pagination.limit.unwrap_or(5);
    if limit > 10 {
        return HttpResponse::BadRequest().body("Pagination limit must be less than 10.");
    }
    let offset = (pagination.page.unwrap_or(1) - 1) * limit;

    let players: Vec<RestV1Player> = Player::find_all_with_offset(&state, offset, limit)
        .await
        .iter()
        .map(RestV1Player::from)
        .collect();
    
    HttpResponse::Ok().json(players)
}

#[get("/{id}")]
pub async fn find(id: web::Path<String>, state: WebserverState) -> impl Responder {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state, id).await;
    match player {
        Some(player) => HttpResponse::Ok().json(RestV1Player::from(&player)),
        None => HttpResponse::NotFound().finish(),
    }
}

#[post("/")]
pub async fn create(data: RestV1PlayerCreation, state: WebserverState) -> impl Responder {
    let query = Player::find(&state, Either::Left(data.uuid)).await;
    if query.is_some() {
        return HttpResponse::Conflict().finish();
    }
    let player = Player::from(data);
    match player.create(&state).await {
        Err(error) => {
            log::error!(
                "An error occurred while processing a player creation request: {:?}",
                error
            );
            HttpResponse::InternalServerError().body("Failed to create a new player.")
        }
        Ok(_) => HttpResponse::Ok().json(RestV1Player::from(&player)),
    }
}

#[delete("/{id}")]
pub async fn delete(id: web::Path<String>, state: WebserverState) -> impl Responder {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state, id).await;
    match player {
        None => HttpResponse::NotFound().finish(),
        Some(player) => {
            if Player::delete(&state, Either::Left(player.uuid)).await.is_err() {
                HttpResponse::InternalServerError().body("Failed to delete a player.")
            } else {
                HttpResponse::Ok().finish()
            }
        }
    }
}