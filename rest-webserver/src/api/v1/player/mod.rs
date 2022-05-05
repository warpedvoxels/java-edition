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
    app::WebServerState,
    entity::{Entity, Player},
    util::{try_either, IntoHttpError}, definitions::rest::RestPlayer,
};

mod dto;
pub use dto::*;

#[post("/")]
pub async fn create(data: RestPlayerCreation, state: WebServerState) -> RestResult<RestPlayer> {
    let query = Player::find(&state.postgres, Either::Left(data.uuid)).await
        .http_internal_error("Failed to find the player.")?;
    if query.is_some() {
        return Ok(Either::Left(HttpResponse::Conflict().finish()));
    }

    let player = Player::from(data);
    player
        .create(&state.postgres)
        .await
        .http_internal_error("Failed to create a new player")?;

    Ok(Either::Right(web::Json(RestPlayer::from(&player))))
}

#[get("/")]
pub async fn find_all(
    pagination: Query<PageInfo>,
    state: WebServerState,
) -> RestResult<Vec<RestPlayer>> {
    let limit = pagination.limit.unwrap_or(5);
    if limit > 10 {
        return Ok(Either::Left(
            HttpResponse::BadRequest().body("Pagination limit must be less than 10."),
        ));
    }
    let offset = (pagination.page.unwrap_or(1) - 1) * limit;

    let players: Vec<RestPlayer> = Player::find_all_with_offset(&state.postgres, offset, limit)
        .await
        .http_internal_error("Failed to find all the players.")?
        .iter()
        .map(RestPlayer::from)
        .collect();

    Ok(Either::Right(web::Json(players)))
}

#[get("/{id}")]
pub async fn find(id: web::Path<String>, state: WebServerState) -> RestResult<RestPlayer> {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state.postgres, id)
        .await
        .http_internal_error("Failed to retrieve the player data.")?
        .http_not_found("Player not found.")?;
    let player = RestPlayer::from(&player);
    Ok(Either::Right(web::Json(player)))
}

#[delete("/{id}")]
pub async fn delete(id: web::Path<String>, state: WebServerState) -> RestResult<RestPlayer> {
    let id = try_either(|| Uuid::from_str(id.trim()), id.to_owned());
    let player = Player::find(&state.postgres, id)
        .await
        .http_internal_error("Failed to retrieve the player data.")?
        .http_not_found("Player not found.")?;

    Player::delete(&state.postgres, Either::Left(player.uuid))
        .await
        .http_internal_error("Failed to delete this player.")?;
    let player = RestPlayer::from(&player);
    
    Ok(Either::Right(web::Json(player)))
}
