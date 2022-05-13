use chrono::Utc;
use grpc_server_common::datatype::Username;
use tonic::{Request, Response, Status};

use crate::definition::{
    entity::Player,
    protocol::{self, player_data_request::Id, PlayerDataReply, PlayerDataRequest, player::PlayerServer},
};

#[derive(Debug)]
pub struct PlayerService;

#[tonic::async_trait]
impl protocol::player::Player for PlayerService {
    async fn retrieve_data(
        &self,
        request: Request<PlayerDataRequest>,
    ) -> Result<Response<PlayerDataReply>, Status> {
        let msg = request.get_ref();
        let uuid = match msg.id.as_ref().unwrap() {
            Id::Username(_) => {
                return Err(Status::invalid_argument("Username is not supported yet."))
            }
            Id::Uuid(id) => id,
        }
        .clone();
        
        let dummy = Player {
            uuid,
            hexes: 0,
            last_username: Username::from("dummy"),
            last_seen: Utc::now().naive_utc(),
            created_at: Utc::now().naive_utc(),
            updated_at: Utc::now().naive_utc(),
        };
        let reply = PlayerDataReply { player: dummy };
        Ok(Response::new(reply))
    }
}

impl PlayerService {
    pub fn service() -> PlayerServer<Self> {
        PlayerServer::new(Self)
    }
}
