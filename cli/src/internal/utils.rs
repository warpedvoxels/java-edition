use std::{
    io::{Error, ErrorKind},
    path::Path,
    process::Stdio,
};

use hexalite_common::prelude::get_hexalite_dir_path;
use tokio::{
    io::{AsyncBufReadExt, BufReader},
    process::Command,
};

pub fn handle_dir_error(src: &Path, dest: &Path, err: Error) {
    if err.kind() != ErrorKind::AlreadyExists {
        panic!(
            "Failed to create {}: {} => {}",
            src.to_str().unwrap(),
            dest.to_str().unwrap(),
            err
        );
    }
}

pub fn use_handling<F>(src: &Path, dest: &Path, func: F)
where
    F: FnOnce(&Path, &Path) -> Result<(), Error>,
{
    func(src, dest).unwrap_or_else(|err| handle_dir_error(src, dest, err));
}

pub fn use_handling_auto<F>(src_path: &Path, path: &str, func: F)
where
    F: FnOnce(&Path, &Path) -> Result<(), Error>,
{
    let hexalite = get_hexalite_dir_path();
    let src = src_path.join(path);
    let dest = hexalite.join(path);
    use_handling(&src, &dest, func)
}

#[allow(unused_must_use)]
pub async fn run_command(command: &str, args: &[&str]) {
    async fn read_lines<T>(lines: &mut tokio::io::Lines<T>)
    where
        T: tokio::io::AsyncBufRead + Unpin,
    {
        loop {
            let line = lines.next_line().await;
            if line.is_err() {
                break;
            }
            if let Some(line) = line.unwrap() {
                println!("{}", line);
            } else {
                break;
            };
        }
    }

    let mut command = Command::new(command)
        .args(args)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .spawn()
        .expect("Failed to spawn command.");

    if let Some(stdout) = command.stdout.take() {
        read_lines(&mut BufReader::new(stdout).lines()).await;
    }
    if let Some(stderr) = command.stderr.take() {
        read_lines(&mut BufReader::new(stderr).lines()).await;
    }
}
