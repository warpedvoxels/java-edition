use std::sync::Arc;

use hexalite_common::{settings::{HexaliteSettings, self}, io::Writer};

pub fn build() -> Arc<HexaliteSettings> {
    let settings = settings::read().expect("Failed to read the settings.");
    if settings.write(&()).is_err() { // Just in case there is new fields in the settings.
        panic!("Failed to write the settings after reading.");
    }
    log::debug!("The settings were read successfully.");
    Arc::new(settings)
}
