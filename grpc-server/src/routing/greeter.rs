use tonic::{Request, Status, Response};
use crate::definition::protocol::{self, greeter::GreeterServer};

#[derive(Debug)]
pub struct Greeter;

#[tonic::async_trait]
impl protocol::greeter::Greeter for Greeter {
    async fn say_hello(&self, request: Request<protocol::HelloRequest>) -> Result<Response<protocol::HelloReply>, Status> {
        println!("Got a request from: {:?}", request.remote_addr());
        let reply = protocol::HelloReply {
            message: format!("Hello, {}!", request.into_inner().name),
        };
        Ok(Response::new(reply))
    }
}

impl Greeter {
    pub fn service() -> GreeterServer<Self> {
        GreeterServer::new(Self)
    }
}
