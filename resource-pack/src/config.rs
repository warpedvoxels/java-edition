use serde::Deserialize;

use crate::get_instrument;

#[derive(Debug, Default, Clone, Deserialize)]
pub struct BlocksConfig {
    pub blocks: Vec<BlocksConfigEntry>,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct BlocksConfigEntry {
    pub parent: String,
    pub texture: BlocksConfigEntryTexture,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct FontConfig {
    pub font: Vec<FontConfigEntry>,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct FontConfigEntry {
    pub char: String,
    pub ascent: u32,
    pub height: u32,
    pub file: String,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct BlocksConfigEntryTexture {
    pub name: String,
    pub index: u32,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct MetadataConfig {
    #[serde(rename = "metadata")]
    pub inner: InnerMetadataConfig,
}

#[derive(Debug, Default, Clone, Deserialize)]
pub struct InnerMetadataConfig {
    pub description: String,
    pub format: u32,
}

impl BlocksConfigEntryTexture {
    pub fn field(&self) -> String {
        let index = self.index + 26;
        format!(
            "instrument={},note={},powered={}",
            get_instrument(index),
            index % 25,
            index >= 400
        )
    }
}
