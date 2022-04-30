use std::str::FromStr;

use actix_web::{
    delete, get, post,
    web::{self, Query},
    Either, HttpResponse,
};
use uuid::Uuid;

use crate::{
    api::v1::RestResult,
    api::PageInfo,
    app::WebserverState,
    entity::{Entity, Player},
    util::{try_either, IntoHttpError}, definitions::rest::RestPlayer,
};

mod dto;
pub use dto::*;

#[post("/")]
pub async fn create(data: RestPlayerCreation, state: WebserverState) -> RestResult<RestPlayer> {
    let query = Player::find(&state.pool, Either::Left(data.uuid)).await;
    if query.is_some() {
        return Ok(Either::Left(HttpResponse::Conflict().finish()));
    }

    let player = Player::from(data);
    player
        .create(&state.pool)
        .await
        .http_internal_error("Failed to create a new player")?;

    Ok(Either::Right(web::Json(RestPlayer::from(&player))))
}

#[get("/")]
pub async fn find_all(
    pagination: Query<PageInfo>,
    state: WebserverState,
) -> RestResult<Vec<RestPlayer>> {
    let limit = pagination.limit.unwrap_or(5);
    if limit > 10 {
        return Ok(Either::Left(
            HttpResponse::BadRequest().body("Pagination limit must be less than 10."),
        ));
    }
    let offset = (pagination.page.unwrap_or(1) - 1) * limit;

    let players: Vec<RestPlayer> = Player::find_all_with_offset(&state.pool, offset, limit)
        .await
        .iter()
        .map(RestPlayer::from)
        .collect();

    Ok(Either::Right(web::Json(players)))
}

#[get("/{id}")]
pub async fn find(id: web::Path<String>, state: WebserverState) -> RestResult<RestPlayer> {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state.pool, id)
        .await
        .http_not_found("Player not found.")?;
    let player = RestPlayer::from(&player);
    Ok(Either::Right(web::Json(player)))
}

#[delete("/{id}")]
pub async fn delete(id: web::Path<String>, state: WebserverState) -> RestResult<RestPlayer> {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state.pool, id)
        .await
        .http_not_found("Player not found.")?;

    Player::delete(&state.pool, Either::Left(player.uuid))
        .await
        .http_internal_error("Failed to delete this player.")?;
    let player = RestPlayer::from(&player);
    
    Ok(Either::Right(web::Json(player)))
}
