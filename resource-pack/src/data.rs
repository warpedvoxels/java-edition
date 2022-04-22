use serde::Serialize;

use crate::{BlocksConfigEntry, FontConfigEntry, MetadataConfig};

#[derive(Debug, Default, Clone, Serialize)]
pub struct BlockState {
    #[serde(rename = "model")]
    pub model_name: String,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct BlockModel {
    pub parent: String,
    pub textures: BlockModelTextures,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct BlockModelTextures {
    #[serde(rename = "all")]
    pub name: String,
}

impl BlockModelTextures {
    pub fn state(&self) -> BlockState {
        let name: String = self
            .name
            .chars()
            .skip(self.name.rfind('/').unwrap_or(0))
            .collect();
        BlockState {
            model_name: format!("block{}", name),
        }
    }
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct PackMeta {
    #[serde(rename = "pack_format")]
    pub format_id: u32,
    pub description: String,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct ItemModel {
    pub parent: String,
    pub textures: ItemModelTextures,
    pub overrides: Vec<ItemModelOverride>,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct ItemModelTextures {
    #[serde(rename = "layer0")]
    pub name: String,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct ItemModelOverride {
    pub model: String,
    pub predicate: ItemModelOverridePredicate,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct ItemModelOverridePredicate {
    pub custom_model_data: u32,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct FontProvider {
    pub file: String,
    pub chars: Vec<String>,
    pub ascent: u32,
    pub height: u32,
    #[serde(rename = "type")]
    pub kind: String,
}

#[derive(Debug, Default, Clone, Serialize)]
pub struct FontProvidersHolder {
    pub providers: Vec<FontProvider>,
}

impl FontProvidersHolder {
    pub fn new(providers: Vec<FontProvider>) -> Self {
        Self { providers }
    }
}

impl ItemModel {
    pub fn paper() -> ItemModel {
        ItemModel {
            parent: String::from("minecraft:item/generated"),
            textures: ItemModelTextures {
                name: String::from("minecraft:item/paper"),
            },
            overrides: vec![],
        }
    }
    pub fn append(&mut self, model: String, index: u32) {
        self.overrides.push(ItemModelOverride {
            model,
            predicate: ItemModelOverridePredicate {
                custom_model_data: index + 1000,
            },
        });
    }
}

impl From<&FontConfigEntry> for FontProvider {
    fn from(entry: &FontConfigEntry) -> Self {
        FontProvider {
            file: entry.file.clone(),
            chars: vec![entry.char.clone()],
            ascent: entry.ascent,
            height: entry.height,
            kind: String::from("bitmap"),
        }
    }
}

impl From<MetadataConfig> for PackMeta {
    fn from(metadata: MetadataConfig) -> Self {
        PackMeta {
            format_id: metadata.inner.format,
            description: metadata.inner.description,
        }
    }
}

impl From<BlocksConfigEntry> for BlockModel {
    fn from(entry: BlocksConfigEntry) -> Self {
        BlockModel {
            parent: String::from("block/cube_all"),
            textures: BlockModelTextures {
                name: entry.texture.name,
            },
        }
    }
}
