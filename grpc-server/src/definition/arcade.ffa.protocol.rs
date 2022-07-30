#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct FfaPlayerStatsDataRequest {
    pub id: ::core::option::Option<ffa_player_stats_data_request::Id>,
}
/// Nested message and enum types in `FFAPlayerStatsDataRequest`.
pub mod ffa_player_stats_data_request {
    #[derive(serde::Serialize, serde::Deserialize)]
    #[serde(rename_all = "snake_case")]
    #[derive(Debug, Clone, PartialEq)]
    pub enum Id {
        Uuid(::uuid::Uuid),
        Username(::prost::alloc::string::String),
    }
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct FfaPlayerStatsDataPatchRequest {
    pub kills: ::core::option::Option<i32>,
    pub deaths: ::core::option::Option<i32>,
    pub assists: ::core::option::Option<i32>,
    pub killstreak: ::core::option::Option<i32>,
    pub longest_killstreak: ::core::option::Option<i32>,
    pub id: ::core::option::Option<ffa_player_stats_data_patch_request::Id>,
}
/// Nested message and enum types in `FFAPlayerStatsDataPatchRequest`.
pub mod ffa_player_stats_data_patch_request {
    #[derive(serde::Serialize, serde::Deserialize)]
    #[serde(rename_all = "snake_case")]
    #[derive(Debug, Clone, PartialEq)]
    pub enum Id {
        Uuid(::uuid::Uuid),
        Username(::prost::alloc::string::String),
    }
}
#[derive(serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "snake_case")]
#[derive(Debug, Clone, PartialEq)]
pub struct FfaPlayerStatsDataReply {
    pub data: super::entity::FfaPlayerStats,
}
pub mod ffa_player_stats {
    use tonic::codegen::*;
    #[async_trait]
    pub trait FfaPlayerStats: Send + Sync + 'static {
        async fn retrieve_data(
            &self,
            request: tonic::Request<super::FfaPlayerStatsDataRequest>,
        ) -> Result<tonic::Response<super::FfaPlayerStatsDataReply>, tonic::Status>;
        async fn modify_data(
            &self,
            request: tonic::Request<super::FfaPlayerStatsDataPatchRequest>,
        ) -> Result<tonic::Response<super::FfaPlayerStatsDataReply>, tonic::Status>;
    }
    #[derive(Debug)]
    pub struct FfaPlayerStatsServer<T: FfaPlayerStats> {
        inner: _Inner<T>,
        accept_compression_encodings: EnabledCompressionEncodings,
        send_compression_encodings: EnabledCompressionEncodings,
    }
    struct _Inner<T>(Arc<T>);
    impl<T: FfaPlayerStats> FfaPlayerStatsServer<T> {
        pub fn from_arc(inner: Arc<T>) -> Self {
            Self {
                inner: _Inner(inner),
                accept_compression_encodings: Default::default(),
                send_compression_encodings: Default::default(),
            }
        }
        pub fn new(inner: T) -> Self {
            Self::from_arc(Arc::new(inner))
        }
        pub fn with_interceptor<F>(
            inner: T,
            interceptor: F,
        ) -> InterceptedService<Self, F>
        where
            F: tonic::service::Interceptor,
        {
            InterceptedService::new(Self::new(inner), interceptor)
        }
        #[must_use]
        pub fn accept_gzip(mut self) -> Self {
            self.accept_compression_encodings.enable_gzip();
            self
        }
        #[must_use]
        pub fn send_gzip(mut self) -> Self {
            self.send_compression_encodings.enable_gzip();
            self
        }
    }
    impl<T, B> tonic::codegen::Service<http::Request<B>> for FfaPlayerStatsServer<T>
    where
        T: FfaPlayerStats,
        B: Body + Send + 'static,
        B::Error: Into<StdError> + Send + 'static,
    {
        type Response = http::Response<tonic::body::BoxBody>;
        type Error = std::convert::Infallible;
        type Future = BoxFuture<Self::Response, Self::Error>;
        fn poll_ready(
            &mut self,
            _cx: &mut Context<'_>,
        ) -> Poll<Result<(), Self::Error>> {
            Poll::Ready(Ok(()))
        }
        fn call(&mut self, req: http::Request<B>) -> Self::Future {
            match req.uri().path() {
                "/arcade.ffa.protocol.FfaPlayerStats/RetrieveData" => {
                    #[allow(non_camel_case_types)]
                    struct RetrieveDataSvc<T: FfaPlayerStats>(pub Arc<T>);
                    impl<
                        T: FfaPlayerStats,
                    > tonic::server::UnaryService<super::FfaPlayerStatsDataRequest>
                    for RetrieveDataSvc<T> {
                        type Response = super::FfaPlayerStatsDataReply;
                        type Future = BoxFuture<
                            tonic::Response<Self::Response>,
                            tonic::Status,
                        >;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::FfaPlayerStatsDataRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { inner.retrieve_data(request).await };
                            Box::pin(fut)
                        }
                    }
                    let accept_compression_encodings = self.accept_compression_encodings;
                    let send_compression_encodings = self.send_compression_encodings;
                    let inner = self.inner.clone();
                    let fut = async move {
                        let inner = inner.0;
                        let method = RetrieveDataSvc(inner);
                        let codec = crate::codec::CborCodec::default();
                        let mut grpc = tonic::server::Grpc::new(codec)
                            .apply_compression_config(
                                accept_compression_encodings,
                                send_compression_encodings,
                            );
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/arcade.ffa.protocol.FfaPlayerStats/ModifyData" => {
                    #[allow(non_camel_case_types)]
                    struct ModifyDataSvc<T: FfaPlayerStats>(pub Arc<T>);
                    impl<
                        T: FfaPlayerStats,
                    > tonic::server::UnaryService<super::FfaPlayerStatsDataPatchRequest>
                    for ModifyDataSvc<T> {
                        type Response = super::FfaPlayerStatsDataReply;
                        type Future = BoxFuture<
                            tonic::Response<Self::Response>,
                            tonic::Status,
                        >;
                        fn call(
                            &mut self,
                            request: tonic::Request<
                                super::FfaPlayerStatsDataPatchRequest,
                            >,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { inner.modify_data(request).await };
                            Box::pin(fut)
                        }
                    }
                    let accept_compression_encodings = self.accept_compression_encodings;
                    let send_compression_encodings = self.send_compression_encodings;
                    let inner = self.inner.clone();
                    let fut = async move {
                        let inner = inner.0;
                        let method = ModifyDataSvc(inner);
                        let codec = crate::codec::CborCodec::default();
                        let mut grpc = tonic::server::Grpc::new(codec)
                            .apply_compression_config(
                                accept_compression_encodings,
                                send_compression_encodings,
                            );
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                _ => {
                    Box::pin(async move {
                        Ok(
                            http::Response::builder()
                                .status(200)
                                .header("grpc-status", "12")
                                .header("content-type", "application/grpc")
                                .body(empty_body())
                                .unwrap(),
                        )
                    })
                }
            }
        }
    }
    impl<T: FfaPlayerStats> Clone for FfaPlayerStatsServer<T> {
        fn clone(&self) -> Self {
            Self {
                inner: self.inner.clone(),
                accept_compression_encodings: self.accept_compression_encodings,
                send_compression_encodings: self.send_compression_encodings,
            }
        }
    }
    impl<T: FfaPlayerStats> Clone for _Inner<T> {
        fn clone(&self) -> Self {
            Self(self.0.clone())
        }
    }
    impl<T: std::fmt::Debug> std::fmt::Debug for _Inner<T> {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            write!(f, "{:?}", self.0)
        }
    }
    impl<T: FfaPlayerStats> tonic::transport::NamedService for FfaPlayerStatsServer<T> {
        const NAME: &'static str = "arcade.ffa.protocol.FfaPlayerStats";
    }
    #[derive(Clone, Debug)]
    pub struct FfaPlayerStatsClient<T> {
        inner: tonic::client::Grpc<T>,
    }
    impl FfaPlayerStatsClient<tonic::transport::Channel> {
        pub async fn connect<D>(dst: D) -> Result<Self, tonic::transport::Error>
        where
            D: std::convert::TryInto<tonic::transport::Endpoint>,
            D::Error: Into<StdError>,
        {
            let conn = tonic::transport::Endpoint::new(dst)?.connect().await?;
            Ok(Self::new(conn))
        }
    }
    impl<T> FfaPlayerStatsClient<T>
    where
        T: tonic::client::GrpcService<tonic::body::BoxBody>,
        T::Error: Into<StdError>,
        T::ResponseBody: Body<Data = Bytes> + Send + 'static,
        <T::ResponseBody as Body>::Error: Into<StdError> + Send,
    {
        pub fn new(inner: T) -> Self {
            Self {
                inner: tonic::client::Grpc::new(inner),
            }
        }
        pub fn with_interceptor<F>(
            inner: T,
            interceptor: F,
        ) -> FfaPlayerStatsClient<InterceptedService<T, F>>
        where
            F: tonic::service::Interceptor,
            T::ResponseBody: Default,
            T: tonic::codegen::Service<
                http::Request<tonic::body::BoxBody>,
                Response = http::Response<
                    <T as tonic::client::GrpcService<tonic::body::BoxBody>>::ResponseBody,
                >,
            >,
            <T as tonic::codegen::Service<
                http::Request<tonic::body::BoxBody>,
            >>::Error: Into<StdError> + Send + Sync,
        {
            FfaPlayerStatsClient::new(InterceptedService::new(inner, interceptor))
        }
        #[must_use]
        pub fn send_gzip(mut self) -> Self {
            self.inner = self.inner.send_gzip();
            self
        }
        #[must_use]
        pub fn accept_gzip(mut self) -> Self {
            self.inner = self.inner.accept_gzip();
            self
        }
        pub async fn retrieve_data(
            &mut self,
            request: impl tonic::IntoRequest<super::FfaPlayerStatsDataRequest>,
        ) -> Result<tonic::Response<super::FfaPlayerStatsDataReply>, tonic::Status> {
            self.inner
                .ready()
                .await
                .map_err(|e| {
                    tonic::Status::new(
                        tonic::Code::Unknown,
                        format!("Service was not ready: {}", e.into()),
                    )
                })?;
            let codec = crate::codec::CborCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/arcade.ffa.protocol.FFAPlayerStats/RetrieveData",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn modify_data(
            &mut self,
            request: impl tonic::IntoRequest<super::FfaPlayerStatsDataPatchRequest>,
        ) -> Result<tonic::Response<super::FfaPlayerStatsDataReply>, tonic::Status> {
            self.inner
                .ready()
                .await
                .map_err(|e| {
                    tonic::Status::new(
                        tonic::Code::Unknown,
                        format!("Service was not ready: {}", e.into()),
                    )
                })?;
            let codec = crate::codec::CborCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/arcade.ffa.protocol.FFAPlayerStats/ModifyData",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
    }
}
