use super::*;

pub fn webserver() {
    let compiled = &*HEXALITE.join("compiled");
    // webserver on unix-like systems and webserver.exe on windows
    let path = if cfg!(target_os = "windows") {
        compiled.join("webserver.exe")
    } else {
        compiled.join("webserver")
    };
    run_command(
        path.to_str()
            .expect("Failed to retrieve the compiled webserver path."),
        &[],
    );
}

pub fn resource_pack() {
    let compiled = &*HEXALITE.join("compiled");
    let path = compiled.join("resource-pack-generator.jar");
    let path = path
        .to_str()
        .expect("Failed to retrieve the compiled resource pack path.");
    run_command("java", &["-jar", path]);
}

pub fn minecraft() {
    let run = &*HEXALITE.join("run");
    let path = run.join("purpur.jar");
    let path = path
        .to_str()
        .expect("Failed to retrieve the path to the Minecraft server.");
    run_command("java", &["-Xmx2G", "-jar", path]);
}
