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

use anyhow::{Context as AnyhowContext, Result};
use poise::builtins::register_in_guild;
use serenity::{GuildId, Ready};

use crate::{command, framework};
use crate::config::VoxeruConfig;
use crate::typealias::*;

pub mod macros;
pub mod messages;
pub mod util;
pub mod welcome_screen;
pub mod event_handler;

async fn on_ready(
    event: &Ready,
    config: &VoxeruConfig,
    ctx: &serenity::Context,
    framework: &PoiseFramework,
) -> Result<()> {
    log::info!("🚀 Logged in as {}", event.user.tag());
    register_in_guild(
        ctx,
        &framework.options().commands,
        GuildId(config.settings.guild.id),
    )
        .await
        .context("Failed to register guild-specific slash commands.")?;
    Ok(())
}

pub async fn init(config: VoxeruConfig) -> Result<()> {
    use command::support::*;

    (framework! {
        token: config.env.discord_auth_token,
        commands: [source(), invite()],
        intents: [guilds, guild_members, guild_messages, message_content],
        edit_tracker_duration: Duration::from_secs(300),
        prefix: config.settings.command_prefix,
        config: config,
        state: || VoxeruState { config },
    })
        .run()
        .await
        .context("Failed to set up Discord bot.")
}
