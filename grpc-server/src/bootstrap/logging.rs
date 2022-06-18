use anyhow::{Context, Result};
use env_logger::{builder, fmt::Color};
use log::Level;
use std::{
    io::Write,
    sync::atomic::{AtomicUsize, Ordering},
};

static WIDTH: AtomicUsize = AtomicUsize::new(0);

fn get_max_width(from: &str) -> usize {
    let max = WIDTH.load(Ordering::Relaxed);
    let f = from.len();
    if max < f {
        WIDTH.store(f, Ordering::Relaxed);
        f
    } else {
        max
    }
}

pub fn init() -> Result<()> {
    if std::env::var("RUST_LOG").is_err() {
        std::env::set_var("RUST_LOG", "debug");
    }
    builder()
        .filter_level(log::LevelFilter::Debug)
        .format(|fmt, record| {
            let target = record.target();
            let padding = get_max_width(target);
            let mut style = fmt.style();
            let time = fmt.timestamp_millis();
            let level = match record.level() {
                Level::Trace => style.set_color(Color::Magenta).value("TRACE"),
                Level::Debug => style.set_color(Color::Blue).value("DEBUG"),
                Level::Info => style.set_color(Color::Green).value("INFO "),
                Level::Warn => style.set_color(Color::Yellow).value("WARN "),
                Level::Error => style.set_color(Color::Red).value("ERROR"),
            };
            let mut style = fmt.style();
            let target =
                style
                    .set_bold(true)
                    .value(format!("{: <width$}", target, width = padding));
            let message = record.args();
            writeln!(fmt, " {time} {level} {target} {message}")
        })
        .try_init()
        .context("Failed to initialize the logging system.")
}
