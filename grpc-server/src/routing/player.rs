use chrono::Utc;
use tonic::{Request, Response, Status};

use crate::definition::{
    entity::Player,
    datatype::id::Data as Id,
    protocol::{self, PlayerDataReply, PlayerDataRequest, player::PlayerServer, PlayerDataPatchRequest},
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
        let uuid = match *msg.id.data.as_ref().unwrap() {
            Id::Username(_) => {
                return Err(Status::invalid_argument("Username is not supported yet."))
            }
            Id::Uuid(id) => id,
        };
        
        let dummy = Player {
            uuid,
            hexes: 0,
            last_username: "dummy".into(),
            last_seen: Utc::now().naive_utc(),
            created_at: Utc::now().naive_utc(),
            updated_at: Utc::now().naive_utc(),
        };
        let reply = PlayerDataReply { data: dummy };
        Ok(Response::new(reply))
    }
    async fn modify_data(&self, request: Request<PlayerDataPatchRequest>) -> Result<Response<PlayerDataReply>, Status> {
        let msg = request.get_ref();
        let uuid = match *msg.id.data.as_ref().unwrap() {
            Id::Username(_) => {
                return Err(Status::invalid_argument("Username is not supported yet."))
            }
            Id::Uuid(id) => id,
        };
        let dummy = Player {
            uuid,
            hexes: 0,
            last_username: "dummy".into(),
            last_seen: Utc::now().naive_utc(),
            created_at: Utc::now().naive_utc(),
            updated_at: Utc::now().naive_utc(),
        };
        let reply = PlayerDataReply { data: dummy };
        Ok(Response::new(reply))
    }
}

impl PlayerService {
    pub fn service() -> PlayerServer<Self> {
        PlayerServer::new(Self)
    }
}
