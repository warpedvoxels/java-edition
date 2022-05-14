use std::path::Path;

use serde::de;

pub fn get_instrument(index: u32) -> &'static str {
    match index / 25 % 384 {
        1 => "basedrum",
        2 => "snare",
        3 => "hat",
        4 => "bass",
        5 => "flute",
        6 => "bell",
        7 => "guitar",
        8 => "chime",
        9 => "xylophone",
        10 => "iron_xylophone",
        11 => "cow_bell",
        12 => "didgeridoo",
        13 => "bit",
        14 => "banjo",
        15 => "pling",
        _ => "harp",
    }
}

pub fn read_and_parse<T>(file_path: &Path) -> T
where
    T: de::DeserializeOwned,
{
    let file_content =
        std::fs::read_to_string(file_path).expect("Failed to read the configuration file.");
    serde_yaml::from_str(&file_content).expect("Failed to parse file")
}

pub fn copy_dir_all(src: impl AsRef<Path>, dst: impl AsRef<Path>) -> std::io::Result<()> {
    std::fs::create_dir_all(&dst)?;
    for entry in std::fs::read_dir(src)? {
        let entry = entry?;
        let ty = entry.file_type()?;
        if ty.is_dir() {
            copy_dir_all(entry.path(), dst.as_ref().join(entry.file_name()))?;
        } else {
            std::fs::copy(entry.path(), dst.as_ref().join(entry.file_name()))?;
        }
    }
    Ok(())
}
