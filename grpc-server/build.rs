use std::fmt::Write;
use std::fs;
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
        writeln!(buf, "pub trait {}: Send + Sync + 'static {{", service.name).unwrap();
    }

    fn generate_server_trait_method(&self, method: &Method, buf: &mut String) {
        let name = &method.name;
        let (req_name, res_name) = self.request_names(method);
        match (method.client_streaming, method.server_streaming) {
            (false, false) => {
                write!(
                    buf,
                    "async fn {}(&self, request: tonic::Request<{}>) ",
                    method.name, req_name
                )
                .unwrap();
                write!(
                    buf,
                    " -> Result<tonic::Response<{}>, tonic::Status>;",
                    res_name
                )
                .unwrap();
            }
            (true, false) => {
                write!(
                    buf,
                    "async fn {}(&self, request: tonic::Request<tonic::Streaming<{}>>) ",
                    method.name, req_name
                )
                .unwrap();
                write!(
                    buf,
                    " -> Result<tonic::Response<tonic::Streaming<{}>>, tonic::Status>;",
                    res_name
                )
                .unwrap();
            }
            (false, true) => {
                writeln!(buf,"type {}Stream = futures_core::Stream<Item = Result<{}, tonic::Status>> + Send + 'static;", name, res_name).unwrap();
                write!(
                    buf,
                    "async fn {}(&self, request: tonic::Request<{}>) ",
                    method.name, req_name
                )
                .unwrap();
                write!(
                    buf,
                    " -> Result<tonic::Response<Self::{}Stream>, tonic::Status>;",
                    name
                )
                .unwrap();
            }
            (true, true) => {
                writeln!(buf,"type {}Stream = futures_core::Stream<Item = Result<{}, tonic::Status>> + Send + 'static;", name, res_name).unwrap();
                write!(
                    buf,
                    "async fn {}(&self, request: tonic::Request<tonic::Streaming<{}>>) ",
                    method.name, req_name
                )
                .unwrap();
                write!(
                    buf,
                    " -> Result<tonic::Response<Self::{}Stream>, tonic::Status>;",
                    name
                )
                .unwrap();
            }
        }
        buf.push('\n');
    }

    fn generate_server_trait_impl_struct(&self, service: &Service, buf: &mut String) {
        buf.push_str("#[derive(Debug)]\n");
        writeln!(
            buf,
            "pub struct {}Server<T: {}> {{",
            service.name, service.name
        )
        .unwrap();
        buf.push_str("inner: _Inner<T>,\n");
        buf.push_str("accept_compression_encodings: EnabledCompressionEncodings,\n");
        buf.push_str("send_compression_encodings: EnabledCompressionEncodings,\n");
        buf.push_str("}\n");
        buf.push_str("struct _Inner<T>(Arc<T>);\n");
    }

    fn generate_server_trait_impl_builder(&self, service: &Service, buf: &mut String) {
        writeln!(
            buf,
            "impl <T: {}> {}Server<T> {{",
            service.name, service.name
        )
        .unwrap();
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
        writeln!(
            buf,
            "fn call(&mut self, request: tonic::Request<{}>) -> Self::Future {{",
            request
        )
        .unwrap();
        buf.push_str("let inner = self.0.clone();\n");
        buf.push_str("let fut = async move {");
        writeln!(buf, "inner.{}(request).await", method.name).unwrap();
        buf.push_str("};\n");
        buf.push_str("Box::pin(fut)\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("let accept_compression_encodings = self.accept_compression_encodings;\n");
        buf.push_str("let send_compression_encodings = self.send_compression_encodings;\n");
        buf.push_str("let inner = self.inner.clone();\n");
        buf.push_str("let fut = async move {\n");
        buf.push_str("let inner = inner.0;\n");
        writeln!(buf, "let method = {}Svc(inner);", method.proto_name).unwrap();
        writeln!(buf, "let codec = {}::default();", self.codec_name).unwrap();
        buf.push_str("let mut grpc = tonic::server::Grpc::new(codec)");
        buf.push_str(
            ".apply_compression_config(accept_compression_encodings, send_compression_encodings);",
        );
        writeln!(buf, "let res = grpc.{}(method, req).await;", function_name).unwrap();
        buf.push_str("Ok(res)\n");
        buf.push_str("};\n");
        buf.push_str("Box::pin(fut)\n");
    }

    fn generate_server_trait_impl_service(&self, service: &Service, buf: &mut String) {
        let package2 = if service.package.is_empty() { "" } else { "." };
        writeln!(
            buf,
            "impl<T, B> tonic::codegen::Service<http::Request<B>> for {}Server<T>",
            service.name
        )
        .unwrap();
        buf.push_str("where\n");
        writeln!(buf, "T: {},", service.name).unwrap();
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
            writeln!(buf, "{} => {{", grpc_path).unwrap();
            buf.push_str("#[allow(non_camel_case_types)]\n");
            writeln!(
                buf,
                "struct {}Svc<T: {}>(pub Arc<T>);",
                method.proto_name, service.name
            )
            .unwrap();
            match (method.client_streaming, method.server_streaming) {
                (false, false) => {
                    writeln!(
                        buf,
                        "impl<T: {}> tonic::server::UnaryService<{}> for {}Svc<T> {{",
                        service.name, req_name, method.proto_name
                    )
                    .unwrap();
                    writeln!(buf, "type Response = {};", res_name).unwrap();
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("unary", &req_name, method, buf);
                }
                (false, true) => {
                    writeln!(
                        buf,
                        "impl<T: {}> tonic::server::ServerStreamingService<{}> for {}Svc<T> {{",
                        service.name, req_name, method.proto_name
                    )
                    .unwrap();
                    writeln!(buf, "type Response = {};", res_name).unwrap();
                    writeln!(buf, "type ResponseStream = T::{}Stream;", method.name).unwrap();
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::ResponseStream>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("server_streaming", &req_name, method, buf);
                }
                (true, false) => {
                    writeln!(
                        buf,
                        "impl<T: {}> tonic::server::ClientStreamingService<{}> for {}Svc<T> {{",
                        service.name, req_name, method.proto_name
                    )
                    .unwrap();
                    writeln!(buf, "type Response = {};", res_name).unwrap();
                    buf.push_str("type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;\n");
                    self.apply_streaming_to_buf("client_streaming", &req_name, method, buf);
                }
                (true, true) => {
                    writeln!(
                        buf,
                        "impl<T: {}> tonic::server::StreamingService<{}> for {}Svc<T> {{",
                        service.name, req_name, method.proto_name
                    )
                    .unwrap();
                    writeln!(buf, "type Response = {};", res_name).unwrap();
                    writeln!(buf, "type ResponseStream = T::{}Stream;", method.name).unwrap();
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
        writeln!(
            buf,
            "impl<T: {}> Clone for {}Server<T> {{",
            service.name, service.name
        )
        .unwrap();

        buf.push_str("fn clone(&self) -> Self {\n");
        buf.push_str("Self {\n");
        buf.push_str("inner: self.inner.clone(),\n");
        buf.push_str("accept_compression_encodings: self.accept_compression_encodings,\n");
        buf.push_str("send_compression_encodings: self.send_compression_encodings,\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        writeln!(buf, "impl<T: {}> Clone for _Inner<T> {{", service.name).unwrap();
        buf.push_str("fn clone(&self) -> Self { Self(self.0.clone()) }\n");
        buf.push_str("}\n");
        buf.push_str("impl<T: std::fmt::Debug> std::fmt::Debug for _Inner<T> {\n");
        buf.push_str("fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {\n");
        buf.push_str("write!(f, \"{:?}\", self.0)\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
        writeln!(
            buf,
            "impl<T: {}> tonic::transport::NamedService for {}Server<T> {{",
            service.name, service.name
        )
        .unwrap();
        let path = format!("{}{}{}", service.package, package2, service.name);
        writeln!(buf, "const NAME: &'static str = \"{}\";", path).unwrap();
        buf.push_str("}\n");
    }

    fn generate_server(&self, service: &Service, buf: &mut String) {
        writeln!(buf, "pub mod {} {{", service.proto_name.to_snake_case()).unwrap();
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
        writeln!(buf, "pub struct {}Client<T> {{", service.name).unwrap();
        buf.push_str("inner: tonic::client::Grpc<T>,\n");
        buf.push_str("}\n");
    }

    fn generate_client_struct_connect_impl(&self, service: &Service, buf: &mut String) {
        writeln!(
            buf,
            "impl {}Client<tonic::transport::Channel> {{",
            service.name
        )
        .unwrap();
        buf.push_str("pub async fn connect<D>(dst: D) -> Result<Self, tonic::transport::Error>\n");
        buf.push_str("where D: std::convert::TryInto<tonic::transport::Endpoint>, D::Error: Into<StdError>, {\n");
        buf.push_str("let conn = tonic::transport::Endpoint::new(dst)?.connect().await?;\n");
        buf.push_str("Ok(Self::new(conn))\n");
        buf.push_str("}\n");
        buf.push_str("}\n");
    }

    #[allow(clippy::too_many_arguments)]
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
        write!(
            buf,
            "pub async fn {}(&mut self, request: {})",
            &method.name, req_type
        )
        .unwrap();
        writeln!(buf, " -> Result<{}, tonic::Status> {{", res_type).unwrap();
        buf.push_str("self.inner.ready().await.map_err(|e| {\n");
        buf.push_str("tonic::Status::new(tonic::Code::Unknown, format!(\"Service was not ready: {}\", e.into()))\n");
        buf.push_str("})?;\n");
        writeln!(buf, "let codec = {}::default();", self.codec_name).unwrap();
        writeln!(
            buf,
            "let path = http::uri::PathAndQuery::from_static({});",
            grpc_path
        )
        .unwrap();
        let into = if into_streaming {
            "into_streaming_request"
        } else {
            "into_request"
        };
        writeln!(
            buf,
            "self.inner.{}(request.{}(), path, codec).await",
            func_name, into
        )
        .unwrap();
        buf.push('}');
    }

    fn generate_client_struct_impl(&self, service: &Service, buf: &mut String) {
        writeln!(buf, "impl<T> {}Client<T>", service.name).unwrap();
        buf.push_str("where T: tonic::client::GrpcService<tonic::body::BoxBody>, T::Error: Into<StdError>,\n");
        buf.push_str("T::ResponseBody: Body<Data = Bytes> + Send + 'static, <T::ResponseBody as Body>::Error: Into<StdError> + Send, {\n");
        buf.push_str("pub fn new(inner: T) -> Self {\n");
        buf.push_str("Self { inner: tonic::client::Grpc::new(inner) }\n");
        buf.push_str("}\n");
        writeln!(buf, "pub fn with_interceptor<F>(inner: T, interceptor: F) -> {}Client<InterceptedService<T, F>>", service.name).unwrap();
        buf.push_str("where F: tonic::service::Interceptor, T::ResponseBody: Default, T: tonic::codegen::Service<\n");
        buf.push_str("http::Request<tonic::body::BoxBody>,\n");
        buf.push_str("Response = http::Response<<T as tonic::client::GrpcService<tonic::body::BoxBody>>::ResponseBody>>,\n");
        buf.push_str("<T as tonic::codegen::Service<http::Request<tonic::body::BoxBody>>>::Error: Into<StdError> + Send + Sync, {\n");
        writeln!(
            buf,
            "{}Client::new(InterceptedService::new(inner, interceptor))",
            service.name
        )
        .unwrap();
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
    println!("cargo:rerun-if-changed=prisma");
    println!("cargo:rerun-if-changed=~/.hexalite/settings.toml");

    let current_dir = std::env::current_dir().unwrap();

    let prisma = current_dir.join("prisma");
    let prisma_scheme = prisma.join("schema.prisma");
    let _ = std::fs::remove_file(&prisma_scheme);
    let mut prisma_files = prisma.read_dir().unwrap();
    let mut prisma_base =
        std::fs::read_to_string(prisma_files.next().unwrap().unwrap().path()).unwrap();

    for entry in prisma_files {
        let entry = entry.unwrap();
        let path = entry.path();
        let content = std::fs::read_to_string(&path).unwrap();
        writeln!(prisma_base, "\n{content}").unwrap();
    }
    fs::write(&prisma_scheme, &prisma_base).unwrap();

    let current_dir = current_dir
        .join("../")
        .canonicalize()
        .unwrap()
        .join("definitions");

    let mut files: Vec<PathBuf> = Vec::new();
    if let Ok(input) = glob::glob("../definitions/**/*.proto") {
        let input = input.filter_map(|value| value.unwrap().canonicalize().ok());
        files.extend(input);
    }
    println!("Files: {}", files.len());

    prost_build::Config::new()
        .type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]")
        .type_attribute(".", "#[serde(rename_all = \"snake_case\")]")
        .extern_path(".google.protobuf.Timestamp", "::chrono::NaiveDateTime")
        .extern_path(".datatype.Uuid", "::uuid::Uuid")
        .out_dir("src/definition")
        .service_generator(Box::new(GrpcServiceGenerator::cbor()))
        .compile_protos(&files, &[current_dir])
        .expect("Failed to compile the protocol buffer definitions");
}
