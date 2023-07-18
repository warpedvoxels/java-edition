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

use anyhow::{Error, Result};
use lazy_static::lazy_static;
use poise::{Event, FrameworkContext, serenity_prelude as serenity2};
use serenity2::{Context, Message};

use crate::discord::{messages, welcome_screen};
use crate::discord::util::reply_with_contents;
use crate::git::{GitHostingService, GitHub};
use crate::typealias::VoxeruState;

lazy_static! {
    static ref GITHUB_SERVICE: GitHub = GitHub::default();
}

pub struct VoxeruEventHandler;

impl VoxeruEventHandler {
    async fn github<'a>(ctx: &'a Context, message: &'a Message) -> Result<()> {
        let parsing = GITHUB_SERVICE.parse_url(&message.content);
        if parsing.is_err() {
            return Ok(());
        }
        let parsing = parsing.unwrap();
        let contents = GITHUB_SERVICE.get_file_contents(&parsing).await;
        if contents.is_err() {
            return Ok(());
        }
        let contents = contents.unwrap();
        if !contents.is_empty() {
            if let Err(error) = reply_with_contents(ctx, message, &contents).await {
                log::error!("Failed to reply with contents: {}", error);
            }
        }
        Ok(())
    }

    async fn message<'a>(
        ctx: &'a Context,
        _framework: &FrameworkContext<'a, VoxeruState, Error>,
        message: &'a Message,
    ) -> Result<()> {
        Self::github(ctx, message).await?;
        Ok(())
    }

    pub async fn handle<'a>(
        context: &'a Context,
        event: &'a Event<'a>,
        framework: FrameworkContext<'a, VoxeruState, Error>,
    ) -> Result<()> {
        let data = framework.user_data;
        match event {
            Event::Message { new_message } => {
                Self::message(context, &framework, new_message).await?
            }
            Event::GuildCreate { guild, .. } if guild.id.0 == data.config.settings.guild.id => {
                welcome_screen::init(context, &guild.id, &data.config).await?;
                messages::init(context, &guild.id, &data.config).await?;
            }
            Event::InteractionCreate { interaction, .. } => {
                messages::handle_interaction(context, interaction, &data.config).await?;
            }
            Event::ThreadUpdate { thread: channel } if channel.thread_metadata.is_some() => {
                let thread = channel.thread_metadata.unwrap();
                if thread.archived && !thread.locked {
                    if let Some(parent_id) = channel.parent_id {
                        if parent_id.0 == data.config.settings.guild.channel_id.general {
                            channel.edit_thread(context, |e|
                                e.archived(false)).await?;
                        }
                    }
                }
            }
            _ => {}
        }
        Ok(())
    }
}
