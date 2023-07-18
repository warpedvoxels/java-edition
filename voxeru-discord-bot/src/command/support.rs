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

use anyhow::{Context, Result};

use crate::config::VoxeruSupportedLanguage;
use crate::locale;
use crate::typealias::*;

macro_rules! hf_command_template {
    ($name:ident) => {
        #[poise::command(slash_command, prefix_command)]
        pub async fn $name(
            ctx: PoiseContext<'_>,
            #[description = "The language the message should display in."] language: Option<
                VoxeruSupportedLanguage,
            >,
        ) -> Result<()> {
            let config = &ctx.data().config;
            let language = language.unwrap_or_default();
            let header = locale!(config, language, commands.$name.header);
            let footer = locale!(config, language, commands.$name.footer);
            let content = format!("{}\n\n{}", header, footer);
            ctx.say(content).await.context(format!(
                "Failed to reply to '{}' command.",
                stringify!($name)
            ))?;
            Ok(())
        }
    };
}

hf_command_template!(source);
hf_command_template!(invite);
