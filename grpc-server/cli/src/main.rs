fn main() {
    std::env::set_current_dir(
        &hexalite_common::dirs::get_source_path()
            .unwrap()
            .join("grpc-server"),
    )
    .unwrap();
    prisma_client_rust_cli::run();
}
