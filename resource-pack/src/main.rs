use std::{
    fs::{self, OpenOptions},
    io::Write,
    path::{Path, PathBuf},
};

use serde_json::json;

use resource_pack::{
    BlockModel, BlocksConfig, copy_dir_all, FontConfig, FontProvider, FontProvidersHolder,
    ItemModel, MetadataConfig, PackMeta, PackMetaHolder, read_and_parse,
};

fn create_dir(path: PathBuf) -> PathBuf {
    if let Err(err) = fs::create_dir_all(&path) {
        if err.kind() != std::io::ErrorKind::AlreadyExists {
            panic!(
                "Failed to create the '{}' directory: {}",
                path.display(),
                err
            );
        }
    }
    path
}

fn write_overwriting(path: &Path, content: String) {
    create_dir(path.parent().unwrap().to_path_buf());
    let mut file = OpenOptions::new()
        .write(true)
        .create(true)
        .open(path)
        .expect("Failed to open the file for writing");
    if let Err(err) = file.write_all(content.as_bytes()) {
        panic!("Failed to write to the file: {}", err);
    }
    if let Err(err) = file.flush() {
        panic!("Failed to flush the file: {}", err);
    }
}

fn main() {
    let resource_pack = hexalite_common::dirs::get_hexalite_dir_path()
        .join("resource-pack")
        .canonicalize()
        .expect("Failed to canonicalize the ~/.hexalite/resource-pack directory");
    let resource_pack_dev = resource_pack.parent().unwrap();

    let metadata: MetadataConfig = read_and_parse(&resource_pack_dev.join("metadata.yml"));
    let blocks: BlocksConfig = read_and_parse(&resource_pack_dev.join("blocks.yml"));
    let font: FontConfig = read_and_parse(&resource_pack_dev.join("font.yml"));

    write_overwriting(
        &resource_pack.join("pack.mcmeta"),
        serde_json::to_string(&PackMetaHolder::from(PackMeta::from(metadata))).unwrap(),
    );

    let assets = resource_pack.join("assets");
    let assets_hexalite = create_dir(assets.join("hexalite"));
    let assets_minecraft = create_dir(assets.join("minecraft"));

    let blockstates = create_dir(assets_minecraft.join("blockstates"));
    let font_dir = create_dir(assets_minecraft.join("font"));
    let models = create_dir(assets_minecraft.join("models"));
    let models_item = create_dir(models.join("item"));

    let mut note_blocks_state = json!({ "variants": {} });
    let note_blocks_state_file = blockstates.join("note_block.json");
    let mut paper = ItemModel::paper();

    for block in blocks.blocks {
        let field = block.texture.field();
        let index = block.texture.index;
        let model = BlockModel::from(block);
        let state = model.textures.state();
        note_blocks_state["variants"][field] = serde_json::to_value(&state).unwrap();
        write_overwriting(
            &models.join(format!("{}.json", &state.model_name)),
            serde_json::to_string(&model).unwrap(),
        );
        paper.append(state.model_name, index);
    }
    write_overwriting(
        &note_blocks_state_file,
        serde_json::to_string(&note_blocks_state).unwrap(),
    );
    write_overwriting(
        &models_item.join("paper.json"),
        serde_json::to_string(&paper).unwrap(),
    );

    let font = FontProvidersHolder::new(font.font.iter().map(FontProvider::from).collect());
    write_overwriting(
        &font_dir.join("default.json"),
        serde_json::to_string(&font).unwrap(),
    );

    let textures_out = assets_hexalite.join("textures");
    let lang_out = assets_hexalite.join("lang");
    let _ = fs::remove_dir(&textures_out);
    let _ = fs::remove_dir(&lang_out);

    copy_dir_all(&resource_pack_dev.join("textures"), &textures_out)
        .expect("Failed to copy textures.");
    copy_dir_all(&resource_pack_dev.join("lang"), &lang_out)
        .expect("Failed to copy language files.");
}
