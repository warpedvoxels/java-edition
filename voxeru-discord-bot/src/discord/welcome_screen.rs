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

use anyhow::Result;
use poise::serenity_prelude::{Context, GuildId};

use crate::config::VoxeruConfig;

macro_rules! create_welcome_channel {
    ($builder:expr, $config:expr, $emoji:literal, $locale_path:ident) => {{
        use poise::serenity_prelude::GuildWelcomeChannelEmoji;

        $builder.create_welcome_channel(|channel| {
            channel
                .id(paste::paste! { $config.settings.guild.channel_id.$locale_path })
                .emoji(GuildWelcomeChannelEmoji::Unicode($emoji.into()))
                .description(&$config.locale.english.description.$locale_path)
        })
    }};
}

pub async fn init(ctx: &Context, guild: &GuildId, config: &VoxeruConfig) -> Result<()> {
    log::info!(
        "Setting up guild welcome screen for guild '{}'.",
        guild.name(ctx).unwrap_or_else(|| guild.to_string())
    );
    guild
        .edit_welcome_screen(ctx, |builder| {
            builder
                .enabled(true)
                .set_welcome_channels(vec![])
                .description(&config.locale.english.description.welcome_screen);
            create_welcome_channel!(builder, config, "🔎", introduction);
            create_welcome_channel!(builder, config, "📜", rules);
            create_welcome_channel!(builder, config, "💬", general);
            create_welcome_channel!(builder, config, "🤷‍♀️", support)
        })
        .await?;
    Ok(())
}
