use std::path::PathBuf;

use heck::ToSnakeCase;
use prost_build::{Method, Service, ServiceGenerator};

const NON_PATH_TYPE_ALLOWLIST: &[&str] = &["()"];

pub struct GrpcServiceGenerator {
    codec_name: String,
}

// Adapted from `tonic-build`
impl GrpcServiceGenerator {
    pub fn cbor() -> Self {
        Self {
            codec_name: String::from("crate::codec::CborCodec"),
        }
    }

    fn request_names(&self, method: &Method) -> (String, String) {
        fn internal(proto_type: &str, rust_type: &str) -> String {
            if proto_type.starts_with(".google.protobuf")
                || rust_type.starts_with("::")
                || NON_PATH_TYPE_ALLOWLIST.iter().any(|ty| *ty == rust_type)
                || rust_type.starts_with("crate::")
            {
                rust_type.to_string()
            } else {
                format!("{}::{}", "super", rust_type)
            }
        }
        (
            internal(&method.input_proto_type, &method.input_type),
            internal(&method.output_proto_type, &method.output_type),
        )
    }

    fn generate_server_start_trait(&self, service: &Service, buf: &mut String) {
        buf.push_str("\nuse tonic::codegen::*;\n");
        buf.push_str("\n#[async_trait]\n");
        buf.push_str(&format!(
            "pub trait {}: Send + Sync + 'static {{\n",
            service.name
        ));
    }

    fn generate_server_trait_method(&self, method: &Method, buf: &mut String) {
        let name = &method.name;
        let (req_name, res_name) = self.request_names(method);
        match (method.client_streaming, method.server_streaming) {
            (false, false) => {
                buf.push_str(&format!(
                    "async fn {}(&self, request: tonic::Request<{}>) ",
                    method.name, req_name
                ));
                buf.push_str(&format!(
                    " -> Result<tonic::Response<{}>, tonic::Status>;",
                    res_name
                ));
            }
            (true, false) => {
                buf.push_str(&format!(
                    "async fn {}(&self, request: tonic::Request<tonic::Streaming<{}>>) ",
                    method.name, req_name
                ));
                buf.push_str(&format!(
                    " -> Result<tonic::Response<tonic::Streaming<{}>>, tonic::Status>;",
                    res_name
                ));
            }
            (false, true) => {
                buf.push_str(&format!("type {}Stream = futures_core::Stream<Item = Result<{}, tonic::Status>> + Send + 'static;\n", name, res_name));
                buf.push_str(&format!(
                    "async fn {}(&self, request: tonic::Request<{}>) ",
                    method.name, req_name
                ));
                buf.push_str(&format!(
                    " -> Result<tonic::Response<Self::{}Stream>, tonic::Status>;",
                    name
                ));
            }
            (true, true) => {
                buf.push_str(&format!("type {}Stream = futures_core::Stream<Item = Result<{}, tonic::Status>> + Send + 'static;\n", name, res_name));
                buf.push_str(&format!(
                    "async fn {}(&self, request: tonic::Request<tonic::Streaming<{}>>) ",
                    method.name, req_name
                ));
                buf.push_str(&format!(
                    " -> Result<tonic::Response<Self::{}Stream>, tonic::Status>;",
                    name
                ));
            }
        }
        buf.push_str("\n");
    }

    fn generate_server_trait_impl_struct(&self, service: &Service, buf: &mut String) {
        buf.push_str("#[derive(Debug)]\n");
        buf.push_str(&format!(
            "pub struct {}Server<T: {}> {{\n",
            service.name, service.name
        ));
        buf.push_str("inner: _Inner<T>,\n");
        buf.push_str("accept_compression_encodings: EnabledCompressionEncodings,\n");
        buf.push_str("send_compression_encodings: EnabledCompressionEncodings,\n");
        buf.push_str("}\n");
        buf.push_str("struct _Inner<T>(Arc<T>);\n");
    }

    fn generate_server_trait_impl_builder(&self, service: &Service, buf: &mut String) {
        buf.push_str(&format!(
            "impl <T: {}> {}Server<T> {{\n",
            service.name, service.name
        ));
        buf.push_str("pub fn from_arc(inner: Arc<T>) -> Self {\n");
        buf.push_str("Self {\n");
        buf.push_str("inner: _Inner(inner),\n");
        buf.push_str("accept_compression_encodings: Default::default(),\n");
        buf.push_str("send_compression_encodings: Default::default(),\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("pub fn new(inner: T) -> Self {\n");
        buf.push_str("Self::from_arc(Arc::new(inner))\n");
        buf.push_str("}\n");
        buf.push_str(
            "pub fn with_interceptor<F>(inner: T, interceptor: F) -> InterceptedService<Self, F>\n",
        );
        buf.push_str("where\n");
        buf.push_str("F: tonic::service::Interceptor,\n");
        buf.push_str("{\n");
        buf.push_str("InterceptedService::new(Self::new(inner), interceptor)\n");
        buf.push_str("}\n");
        buf.push_str("#[must_use]\n");
        buf.push_str("pub fn accept_gzip(mut self) -> Self {\n");
        buf.push_str("self.accept_compression_encodings.enable_gzip();\n");
        buf.push_str("self\n");
        buf.push_str("}\n");
        buf.push_str("#[must_use]\n");
        buf.push_str("pub fn send_gzip(mut self) -> Self {\n");
        buf.push_str("self.send_compression_encodings.enable_gzip();\n");
        buf.push_str("self\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
    }

    fn apply_streaming_to_buf(
        &self,
        function_name: &str,
        request: &str,
        method: &Method,
        buf: &mut String,
    ) {
        buf.push_str(&format!(
            "fn call(&mut self, request: tonic::Request<{}>) -> Self::Future {{\n",
            request
        ));
        buf.push_str("let inner = self.0.clone();\n");
        buf.push_str("let fut = async move {");
        buf.push_str(&format!("inner.{}(request).await\n", method.name));
        buf.push_str("};\n");
        buf.push_str("Box::pin(fut)\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("let accept_compression_encodings = self.accept_compression_encodings;\n");
        buf.push_str("let send_compression_encodings = self.send_compression_encodings;\n");
        buf.push_str("let inner = self.inner.clone();\n");
        buf.push_str("let fut = async move {\n");
        buf.push_str("let inner = inner.0;\n");
        buf.push_str(&format!("let method = {}Svc(inner);\n", method.proto_name));
        buf.push_str(&format!("let codec = {}::default();\n", self.codec_name));
        buf.push_str("let mut grpc = tonic::server::Grpc::new(codec)");
        buf.push_str(
            ".apply_compression_config(accept_compression_encodings, send_compression_encodings);",
        );
        buf.push_str(&format!(
            "let res = grpc.{}(method, req).await;\n",
            function_name
        ));
        buf.push_str("Ok(res)\n");
        buf.push_str("};\n");
        buf.push_str("Box::pin(fut)\n");
    }

    fn generate_server_trait_impl_service(&self, service: &Service, buf: &mut String) {
        let package2 = if service.package.is_empty() { "" } else { "." };
        buf.push_str(&format!(
            "impl<T, B> tonic::codegen::Service<http::Request<B>> for {}Server<T>\n",
            service.name
        ));
        buf.push_str("where\n");
        buf.push_str(&format!("T: {},\n", service.name));
        buf.push_str("B: Body + Send + 'static,\n");
        buf.push_str("B::Error: Into<StdError> + Send + 'static,\n");
        buf.push_str("{\n");
        buf.push_str("type Response = http::Response<tonic::body::BoxBody>;\n");
        buf.push_str("type Error = std::convert::Infallible;\n");
        buf.push_str("type Future = BoxFuture<Self::Response, Self::Error>;\n");
        buf.push_str(
            "fn poll_ready(&mut self, _cx: &mut Context<'_>) -> Poll<Result<(), Self::Error>> {",
        );
        buf.push_str("Poll::Ready(Ok(()))\n");
        buf.push_str("}\n");
        buf.push_str("   fn call(&mut self, req: http::Request<B>) -> Self::Future {\n");
        buf.push_str("match req.uri().path() {\n");
        for method in &service.methods {
            let grpc_path = format!(
                "\"/{}{}{}/{}\"",
                service.package, package2, service.name, method.proto_name
            );

            let (req_name, res_name) = self.request_names(method);
            buf.push_str(&format!("{} => {{\n", grpc_path));
            buf.push_str("#[allow(non_camel_case_types)]\n");
            buf.push_str(&format!(
                "struct {}Svc<T: {}>(pub Arc<T>);\n",
                method.proto_name, service.name
            ));
            match (method.client_streaming, method.server_streaming) {
                (false, false) => {
                    buf.push_str(&format!(
                        "impl<T: {}> tonic::server::UnaryService<{}> for {}Svc<T> {{\n",
                        service.name, req_name, method.proto_name
                    ));
                    buf.push_str(&format!("type Response = {};\n", res_name));
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("unary", &req_name, method, buf);
                }
                (false, true) => {
                    buf.push_str(&format!(
                        "impl<T: {}> tonic::server::ServerStreamingService<{}> for {}Svc<T> {{\n",
                        service.name, req_name, method.proto_name
                    ));
                    buf.push_str(&format!("type Response = {};\n", res_name));
                    buf.push_str(&format!(
                        "type ResponseStream = T::{}Stream;\n",
                        method.name
                    ));
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::ResponseStream>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("server_streaming", &req_name, method, buf);
                }
                (true, false) => {
                    buf.push_str(&format!(
                        "impl<T: {}> tonic::server::ClientStreamingService<{}> for {}Svc<T> {{\n",
                        service.name, req_name, method.proto_name
                    ));
                    buf.push_str(&format!("type Response = {};\n", res_name));
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("client_streaming", &req_name, method, buf);
                }
                (true, true) => {
                    buf.push_str(&format!(
                        "impl<T: {}> tonic::server::StreamingService<{}> for {}Svc<T> {{\n",
                        service.name, req_name, method.proto_name
                    ));
                    buf.push_str(&format!("type Response = {};\n", res_name));
                    buf.push_str(&format!(
                        "type ResponseStream = T::{}Stream;\n",
                        method.name
                    ));
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::ResponseStream>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("streaming", &req_name, method, buf);
                }
            }
        }
        buf.push_str("},\n");
        buf.push_str("_ => Box::pin(async move {\n");
        buf.push_str("Ok(http::Response::builder()\n");
        buf.push_str(".status(200)\n");
        buf.push_str(".header(\"grpc-status\", \"12\")\n");
        buf.push_str(".header(\"content-type\", \"application/grpc\")\n");
        buf.push_str(".body(empty_body())\n");
        buf.push_str(".unwrap())\n");
        buf.push_str("}),\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str(&format!(
            "impl<T: {}> Clone for {}Server<T> {{\n",
            service.name, service.name
        ));

        buf.push_str("fn clone(&self) -> Self {\n");
        buf.push_str("Self {\n");
        buf.push_str("inner: self.inner.clone(),\n");
        buf.push_str("accept_compression_encodings: self.accept_compression_encodings.clone(),\n");
        buf.push_str("send_compression_encodings: self.send_compression_encodings.clone(),\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str(&format!(
            "impl<T: {}> Clone for _Inner<T> {{\n",
            service.name
        ));
        buf.push_str("fn clone(&self) -> Self { Self(self.0.clone()) }\n");
        buf.push_str("}\n");
        buf.push_str("impl<T: std::fmt::Debug> std::fmt::Debug for _Inner<T> {\n");
        buf.push_str("fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {\n");
        buf.push_str("write!(f, \"{:?}\", self.0)\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str(&format!(
            "impl<T: {}> tonic::transport::NamedService for {}Server<T> {{\n",
            service.name, service.name
        ));
        let path = format!("{}{}{}", service.package, package2, service.name);
        buf.push_str(&format!("const NAME: &'static str = \"{}\";\n", path));
        buf.push_str("}\n");
    }

    fn generate_server(&self, service: &Service, buf: &mut String) {
        buf.push_str(&format!(
            "pub mod {} {{\n",
            service.proto_name.to_snake_case()
        ));
        self.generate_server_start_trait(service, buf);
        for method in service.methods.iter() {
            self.generate_server_trait_method(method, buf);
        }
        buf.push_str("}\n");
        self.generate_server_trait_impl_struct(service, buf);
        self.generate_server_trait_impl_builder(service, buf);
        self.generate_server_trait_impl_service(service, buf);
    }

    fn generate_client_start_struct(&self, service: &Service, buf: &mut String) {
        buf.push_str("#[derive(Clone, Debug)]\n");
        buf.push_str(&format!("pub struct {}Client<T> {{\n", service.name));
        buf.push_str("inner: tonic::client::Grpc<T>,\n");
        buf.push_str("}\n");
    }

    fn generate_client_struct_connect_impl(&self, service: &Service, buf: &mut String) {
        buf.push_str(&format!(
            "impl {}Client<tonic::transport::Channel> {{\n",
            service.name
        ));
        buf.push_str("pub async fn connect<D>(dst: D) -> Result<Self, tonic::transport::Error>\n");
        buf.push_str("where D: std::convert::TryInto<tonic::transport::Endpoint>, D::Error: Into<StdError>, {\n");
        buf.push_str("let conn = tonic::transport::Endpoint::new(dst)?.connect().await?;\n");
        buf.push_str("Ok(Self::new(conn))\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
    }

    fn generate_client_struct_impl_method(
        &self,
        service: &Service,
        method: &Method,
        func_name: &str,
        request_streaming: bool,
        response_streaming: bool,
        into_streaming: bool,
        buf: &mut String,
    ) {
        let grpc_path = format!(
            "\"/{}{}{}/{}\"",
            service.package,
            if service.package.is_empty() { "" } else { "." },
            service.proto_name,
            method.proto_name
        );
        let (req_name, res_name) = self.request_names(method);
        let req_type = if request_streaming {
            format!("tonic::IntoStreamingRequest<Message = {}>", req_name)
        } else {
            format!("impl tonic::IntoRequest<{}>", req_name)
        };
        let res_type = if response_streaming {
            format!("tonic::Response<tonic::codec::Streaming<{}>", res_name)
        } else {
            format!("tonic::Response<{}>", res_name)
        };
        buf.push_str(&format!(
            "pub async fn {}(&mut self, request: {})",
            method.name, req_type
        ));
        buf.push_str(&format!(" -> Result<{}, tonic::Status> {{\n", res_type));
        buf.push_str("self.inner.ready().await.map_err(|e| {\n");
        buf.push_str("tonic::Status::new(tonic::Code::Unknown, format!(\"Service was not ready: {}\", e.into()))\n");
        buf.push_str("})?;\n");
        buf.push_str(&format!("let codec = {}::default();\n", self.codec_name));
        buf.push_str(&format!(
            "let path = http::uri::PathAndQuery::from_static({});\n",
            grpc_path
        ));
        let into = if into_streaming {
            "into_streaming_request"
        } else {
            "into_request"
        };
        buf.push_str(&format!(
            "self.inner.{}(request.{}(), path, codec).await\n",
            func_name, into
        ));
        buf.push_str("}");
    }

    fn generate_client_struct_impl(&self, service: &Service, buf: &mut String) {
        buf.push_str(&format!("impl<T> {}Client<T>\n", service.name));
        buf.push_str("where T: tonic::client::GrpcService<tonic::body::BoxBody>, T::Error: Into<StdError>,\n");
        buf.push_str("T::ResponseBody: Body<Data = Bytes> + Send + 'static, <T::ResponseBody as Body>::Error: Into<StdError> + Send, {\n");
        buf.push_str("pub fn new(inner: T) -> Self {\n");
        buf.push_str("Self { inner: tonic::client::Grpc::new(inner) }\n");
        buf.push_str("}\n");
        buf.push_str(
            &format!("pub fn with_interceptor<F>(inner: T, interceptor: F) -> {}Client<InterceptedService<T, F>>\n", service.name)
        );
        buf.push_str("where F: tonic::service::Interceptor, T::ResponseBody: Default, T: tonic::codegen::Service<\n");
        buf.push_str("http::Request<tonic::body::BoxBody>,\n");
        buf.push_str("Response = http::Response<<T as tonic::client::GrpcService<tonic::body::BoxBody>>::ResponseBody>>,\n");
        buf.push_str("<T as tonic::codegen::Service<http::Request<tonic::body::BoxBody>>>::Error: Into<StdError> + Send + Sync, {\n");
        buf.push_str(&format!(
            "{}Client::new(InterceptedService::new(inner, interceptor))\n",
            service.name
        ));
        buf.push_str("}\n");
        buf.push_str("#[must_use]\n");
        buf.push_str("pub fn send_gzip(mut self) -> Self {\n");
        buf.push_str("self.inner = self.inner.send_gzip();");
        buf.push_str("self\n");
        buf.push_str("}\n");
        buf.push_str("#[must_use]\n");
        buf.push_str("pub fn accept_gzip(mut self) -> Self {\n");
        buf.push_str("self.inner = self.inner.accept_gzip();");
        buf.push_str("self\n");
        buf.push_str("}\n");
        for method in &service.methods {
            match (method.client_streaming, method.server_streaming) {
                (false, false) => self.generate_client_struct_impl_method(
                    service, method, "unary", false, false, false, buf,
                ),
                (false, true) => self.generate_client_struct_impl_method(
                    service,
                    method,
                    "server_streaming",
                    false,
                    true,
                    false,
                    buf,
                ),
                (true, false) => self.generate_client_struct_impl_method(
                    service,
                    method,
                    "client_streaming",
                    true,
                    false,
                    true,
                    buf,
                ),
                (true, true) => self.generate_client_struct_impl_method(
                    service,
                    method,
                    "streaming",
                    true,
                    true,
                    true,
                    buf,
                ),
            }
        }
        buf.push_str("}\n");
    }

    fn generate_client(&self, service: &Service, buf: &mut String) {
        self.generate_client_start_struct(service, buf);
        self.generate_client_struct_connect_impl(service, buf);
        self.generate_client_struct_impl(service, buf);
        buf.push_str("}\n");
    }

    fn generate_internally(&self, service: &Service, buf: &mut String) {
        self.generate_server(service, buf);
        self.generate_client(service, buf);
    }
}

impl ServiceGenerator for GrpcServiceGenerator {
    fn generate(&mut self, service: Service, buf: &mut String) {
        let mut new_buf = String::new();
        self.generate_internally(&service, &mut new_buf);
        let syntax_tree = syn::parse_file(&new_buf)
            .expect("Failed to convert generated code into a syntax tree.");
        let formatted_buf = prettyplease::unparse(&syntax_tree);
        buf.push_str(&formatted_buf);
    }
}

fn main() {
    println!("cargo:rerun-if-changed=../definitions");
    println!("cargo:rerun-if-changed=build.rs");

    let working_directory = std::env::current_dir()
        .unwrap()
        .join("../")
        .canonicalize()
        .unwrap();
    let working_directory = working_directory.join("definitions");

    let mut files: Vec<PathBuf> = Vec::new();
    if let Ok(input) = glob::glob("../definitions/**/*.proto") {
        let input = input.filter_map(|value| value.unwrap().canonicalize().ok());
        files.extend(input);
    }
    println!("Files: {}", files.len());

    prost_build::Config::new()
        .type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]")
        .type_attribute(".", "#[serde(rename_all = \"snake_case\")]")
        //.type_attribute(".entity", "#[sea_query::enum_def(suffix = \"TypeDef\")]")
        .extern_path(
            ".google.protobuf.Timestamp",
            "::chrono::DateTime<::chrono::Utc>",
        )
        .extern_path(".datatype.Uuid", "::uuid::Uuid")
        .out_dir("src/definition")
        .service_generator(Box::new(GrpcServiceGenerator::cbor()))
        .compile_protos(&files, &[working_directory])
        .expect("Failed to compile the protocol buffer definitions");
}
