/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

use std::path::Path;

use anyhow::{Context as AnyhowContext, Result};
use poise::serenity_prelude::{AttachmentType, ChannelId, Context, Message};

use crate::git::FileContents;

pub async fn send_image<'a>(
    ctx: &Context, channel_id: &ChannelId, image: &'a Path,
) -> Result<()> {
    channel_id.send_message(ctx, |builder| {
        builder.add_file(AttachmentType::Path(image))
    }).await?;
    Ok(())
}

pub async fn reply_with_contents(
    ctx: &Context, message: &Message, contents: &FileContents,
) -> Result<()> {
    if let Some(guild_id) = message.guild_id {
        let app = guild_id
            .member(ctx, ctx.cache.current_user_id())
            .await
            .expect("Failed to get bot member.");
        let permissions = app
            .permissions(ctx)
            .context("Failed to get bot permissions.")?;
        if !permissions.send_messages() {
            return Ok(());
        }
    }
    let temp_path = std::env::temp_dir().join(&contents.file_name);
    std::fs::write(&temp_path, &contents.value)
        .context("Failed to write file to temporary file.")?;
    let line_range = if let Some(range) = contents.line_range {
        let line_count = if range.1 - range.0 == 0 {
            1
        } else {
            range.1 - range.0
        };
        format!(" (🧵{}-{}, {})", range.0, range.1, line_count)
    } else {
        String::new()
    };
    if line_range.is_empty() {
        log::info!("Sending message with attachment: {}", contents.file_name);
    } else {
        log::info!(
            "Sending message with attachment: {}{}",
            contents.file_name,
            line_range
        );
    }
    let content = format!("**{}{}**", contents.file_name, line_range);
    let result = message
        .channel_id
        .send_message(ctx, |builder| {
            builder
                .reference_message(message)
                .content(content)
                .add_file(AttachmentType::Path(&temp_path))
        })
        .await;
    std::fs::remove_file(&temp_path)
        .context("Failed to delete temporary file.")?;
    result.context("Failed to send message with attachment.")?;
    Ok(())
}
