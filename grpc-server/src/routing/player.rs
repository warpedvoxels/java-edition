use std::{str::FromStr, sync::Arc};

use tonic::{Request, Response, Status};
use uuid::Uuid;

use crate::{
    app::HexaliteGrpcServer,
    definition::{
        datatype::id::Data as Id,
        protocol::{
            self, player::PlayerServer, PlayerDataPatchRequest, PlayerDataReply, PlayerDataRequest,
        },
    },
    prisma::player::{self, Data as PlayerQueryData},
    prisma_attr,
    utils::IntoFixed,
};

impl From<PlayerQueryData> for crate::definition::entity::Player {
    fn from(data: PlayerQueryData) -> Self {
        Self {
            uuid: Uuid::from_str(&data.id).unwrap(),
            hexes: data.hexes,
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
            .data
            .to_owned()
            .ok_or_else(|| Status::invalid_argument("The player ID was not given."))?
        {
            Id::Username(username) => player::last_username::equals(username),
            Id::Uuid(id) => player::id::equals(id.to_string()),
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
        let res = self
            .retrieve_data(Request::new(PlayerDataRequest { id: msg.id.clone() }))
            .await?;
        let attributes = prisma_attr!(
            msg => hexes:
                msg.hexes.unwrap(),
                last_seen: msg.last_seen.unwrap().fixed(),
                updated_at: msg.updated_at.unwrap().fixed(),
                created_at: msg.created_at.unwrap().fixed()
        );

        match self
            .app
            .sql
            .player()
            .find_unique(player::id::equals(res.get_ref().data.uuid.to_string()))
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
}

impl PlayerService {
    pub fn service(app: Arc<HexaliteGrpcServer>) -> PlayerServer<Self> {
        PlayerServer::new(Self { app })
    }
}
