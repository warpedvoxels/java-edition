use env_logger::builder;

pub fn init() {
    builder().filter_level(log::LevelFilter::Debug).try_init().expect("Failed to initialize the logging system.");
    log::debug!("Successfully initialized the logging system!");
}
