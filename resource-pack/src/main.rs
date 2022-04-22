use std::{
    fs::{self, OpenOptions},
    io::Write,
    path::{Path, PathBuf},
};

use resource_pack::{
    read_and_parse, BlockModel, BlocksConfig, FontConfig, FontProvider, FontProvidersHolder,
    ItemModel, MetadataConfig, PackMeta, copy_dir_all,
};
use serde_json::json;

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
    let home = home::home_dir().expect("Failed to get home directory");

    let resource_pack = home
        .join(".hexalite")
        .join("resource-pack")
        .canonicalize()
        .expect("Failed to canonicalize the ~/.hexalite/resource-pack directory");

    let metadata: MetadataConfig = read_and_parse(&resource_pack.join("metadata.yml"));
    let blocks: BlocksConfig = read_and_parse(&resource_pack.join("blocks.yml"));
    let font: FontConfig = read_and_parse(&resource_pack.join("font.yml"));

    let out_dir = create_dir(resource_pack.join("out"));
    write_overwriting(
        &out_dir.join("pack.mcmeta"),
        serde_json::to_string(&PackMeta::from(metadata)).unwrap(),
    );

    let assets_minecraft = create_dir(out_dir.join("assets").join("minecraft"));
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
            serde_json::to_string_pretty(&model).unwrap(),
        );
        paper.append(state.model_name, index);
    }
    write_overwriting(&note_blocks_state_file, serde_json::to_string_pretty(&note_blocks_state).unwrap());
    write_overwriting(
        &models_item.join("paper.json"),
        serde_json::to_string_pretty(&paper).unwrap(),
    );

    let font = FontProvidersHolder::new(font.font.iter().map(FontProvider::from).collect());
    write_overwriting(
        &font_dir.join("default.json"),
        serde_json::to_string_pretty(&font).unwrap(),
    );

    let textures_out = out_dir.join("textures");
    let lang_out = out_dir.join("lang");
    let _ = fs::remove_dir(&textures_out);
    let _ = fs::remove_dir(&lang_out);

    copy_dir_all(&resource_pack.join("textures"), &textures_out).expect("Failed to copy textures");
    copy_dir_all(&resource_pack.join("lang"), &lang_out).expect("Failed to copy lang");
}
