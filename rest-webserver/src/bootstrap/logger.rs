use fast_log::{config::Config, init as fast_log_init};

pub fn init() {
    let config = Config::new().console();
    fast_log_init(config).expect("Failed to initialize the logging system.");
}
