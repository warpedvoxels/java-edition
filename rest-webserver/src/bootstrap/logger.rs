use env_logger::builder;

pub fn init() {
    if std::env::var("RUST_LOG").is_err() {
        std::env::set_var("RUST_LOG", "debug");
    }
    builder()
        .filter_level(log::LevelFilter::Debug)
        .try_init()
        .expect("Failed to initialize the logging system.");
    log::debug!("Successfully initialized the logging system!");
}
