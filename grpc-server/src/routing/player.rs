use std::{str::FromStr, sync::Arc};
use chrono::Utc;

use tonic::{Request, Response, Status};
use uuid::Uuid;

use crate::{
    app::HexaliteGrpcServer,
    definition::{
        protocol::{
            self, player::PlayerServer, PlayerDataPatchRequest, PlayerDataReply, PlayerDataRequest,
        },
    },
    prisma::player::Data as PlayerQueryData,
    prisma_attr,
    utils::IntoFixed,
};
use crate::definition::protocol::PlayerCreateRequest;
use crate::prisma::player::{id, last_username, last_seen};

impl From<PlayerQueryData> for crate::definition::entity::Player {
    fn from(data: PlayerQueryData) -> Self {
        Self {
            uuid: Uuid::from_str(&data.id).unwrap(),
            hexes: data.hexes as u32,
            last_seen: data.last_seen.naive_utc(),
            created_at: data.created_at.naive_utc(),
            updated_at: data.updated_at.naive_utc(),
            last_username: data.last_username,
        }
    }
}

#[derive(Debug)]
pub struct PlayerService {
    app: Arc<HexaliteGrpcServer>,
}

#[tonic::async_trait]
impl protocol::player::Player for PlayerService {
    async fn retrieve_data(
        &self,
        request: Request<PlayerDataRequest>,
    ) -> Result<Response<PlayerDataReply>, Status> {
        let msg = request.get_ref();
        let id = match msg
            .id
            .to_owned()
            .ok_or_else(|| Status::invalid_argument("The player ID was not given."))?
        {
            protocol::player_data_request::Id::Username(username) => last_username::equals(username),
            protocol::player_data_request::Id::Uuid(id) => id::equals(id.to_string()),
        };

        match self.app.sql.player().find_first(vec![id]).exec().await {
            Ok(player) => player
                .ok_or_else(|| Status::not_found("Player not found."))
                .map(|data| Response::new(PlayerDataReply { data: data.into() })),
            Err(error) => {
                log::error!("ISE: Failed to retrieve a player, {error}");
                Err(Status::internal(
                    "An internal error occurred while retrieving a player.",
                ))
            }
        }
    }
    async fn modify_data(
        &self,
        request: Request<PlayerDataPatchRequest>,
    ) -> Result<Response<PlayerDataReply>, Status> {
        let msg = request.get_ref();
        let id = msg.id.as_ref().ok_or_else(|| Status::invalid_argument("The player ID was not given."))?;
        let res = self
            .retrieve_data(Request::new(PlayerDataRequest {
                id: Some(match id {
                    protocol::player_data_patch_request::Id::Username(rcv) => protocol::player_data_request::Id::Username(rcv.clone()),
                    protocol::player_data_patch_request::Id::Uuid(rcv) => protocol::player_data_request::Id::Uuid(*rcv),
                })
            }))
            .await?;
        let attributes = prisma_attr!(
            msg => hexes:         msg.hexes.unwrap(),
                   last_username: msg.last_username.to_owned().unwrap(),
                   last_seen:     msg.last_seen.unwrap().fixed(),
                   updated_at:    msg.updated_at.unwrap().fixed()
        );

        match self
            .app
            .sql
            .player()
            .find_unique(id::equals(res.get_ref().data.uuid.to_string()))
            .update(attributes)
            .exec()
            .await
        {
            Ok(player) => player
                .ok_or_else(|| Status::not_found("Player not found. [modifying context]"))
                .map(|data| Response::new(PlayerDataReply { data: data.into() })),
            Err(error) => {
                log::error!("ISE: Failed to modify a player, {error}");
                Err(Status::internal(
                    "An internal error occurred while modifying a player.",
                ))
            }
        }
    }
    async fn create(&self, request: Request<PlayerCreateRequest>) -> Result<Response<PlayerDataReply>, Status> {
        let msg = request.get_ref();
        let res = self
            .retrieve_data(Request::new(PlayerDataRequest {
                id: Some(protocol::player_data_request::Id::Uuid(msg.uuid))
            }))
            .await;
        if res.is_ok() {
            return Err(Status::already_exists("Cannot create an account for a player that already is registered."));
        }
        match self.app
            .sql
            .player()
            .create(
                id::set(msg.uuid.to_string()),
                last_username::set(msg.username.clone()),
                last_seen::set(Utc::now().naive_utc().fixed()),
                vec![]
            )
            .exec()
            .await
        {
            Ok(player) => Ok(Response::new(PlayerDataReply { data: player.into() })),
            Err(error) => {
                log::error!("ISE: Failed to create a player, {error}");
                Err(Status::internal(
                    "An internal error occurred while modifying a player.",
                ))
            }
        }
    }
}

impl PlayerService {
    pub fn service(app: Arc<HexaliteGrpcServer>) -> PlayerServer<Self> {
        PlayerServer::new(Self { app })
    }
}
