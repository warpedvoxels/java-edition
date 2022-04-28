#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DiscordAuthenticationReply {
    #[prost(string, tag="1")]
    pub access_token: ::prost::alloc::string::String,
    #[prost(string, tag="2")]
    pub refresh_token: ::prost::alloc::string::String,
    #[prost(int32, tag="4")]
    pub expires_in: i32,
    #[prost(string, tag="5")]
    pub scope: ::prost::alloc::string::String,
}
