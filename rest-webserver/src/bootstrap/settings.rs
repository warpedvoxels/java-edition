use std::sync::Arc;

use crate::{settings::{self, WebserverSettings}, io::Writer};

pub fn build() -> Arc<WebserverSettings> {
    let settings = settings::read().expect("Failed to read the settings.");
    if let Err(_) = settings.write(&()) { // Just in case there is new fields in the settings.
        panic!("Failed to write the settings after reading.");
    }
    log::info!("The settings were read successfully.");
    Arc::new(settings)
}
