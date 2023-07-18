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

use anyhow::{bail, Context as AnyhowContext, Result};
use poise::{async_trait, serenity_prelude as serenity};
use poise::serenity_prelude::MessageComponentInteraction;
use serenity::{Context, GuildId, Interaction, InteractionResponseType};
use serenity::model::application::interaction::MessageFlags;

use crate::config::{VoxeruConfig, VoxeruSupportedLanguage};
use crate::locale;

macro_rules! send_message {
    ($ctx:expr, $config:expr, $channels:expr, $topic_key:ident) => {
        {
            use poise::serenity_prelude::{ChannelId, ReactionType};
            use std::str::FromStr;

            let channel = $channels.get(&ChannelId($config.settings.guild.channel_id.$topic_key))
                .context("Failed to get channel.")?;
            let bot_id = $ctx.cache.current_user().id;
            let messages = channel.messages($ctx, |b| b).await?;
            for message in messages.iter() {
                if message.author.id == bot_id {
                    message.delete($ctx).await.context("Failed to delete message.")?;
                }
            }
            $crate::discord::util::send_image(
                $ctx, &channel.id, &$config.settings.asset_path.header.$topic_key
            ).await.context("Failed to send header image.")?;
            channel.send_message($ctx, |builder| {
                builder
                    .embed(|embed| {
                        embed.title(format!("🌐 {}", &$config.locale.english.topic.$topic_key.title))
                            .color($config.settings.accent_color)
                            .description(&$config.locale.english.topic.internalisation)
                    })
                    .components(|components| {
                        components.create_action_row(|row| {
                            row.create_select_menu(|menu| {
                                menu.custom_id("language")
                                    .placeholder("No language selected.")
                                    .options(|options| {
                                        $crate::config::VoxeruSupportedLanguage::values()
                                            .iter()
                                            .for_each(|lang| {
                                                options.create_option(|o|
                                                    o.emoji(ReactionType::from_str(lang.emoji()).unwrap())
                                                        .label(lang.source_name())
                                                        .value(lang.id()));
                                            });
                                        options
                                   })
                            })
                        })
                    })
            }).await.context("Failed to send multi-language message.")?
        }
    }
}

pub async fn init(ctx: &Context, guild: &GuildId, config: &VoxeruConfig) -> Result<()> {
    log::info!("Sending introduction and rules messages for guild '{}'.", guild.name(ctx)
        .unwrap_or_else(|| guild.0.to_string()));
    let channels = guild.channels(ctx).await
        .context("Failed to get guild channels.")?;
    send_message!(ctx, config, channels, introduction);
    send_message!(ctx, config, channels, rules);
    Ok(())
}

#[async_trait]
trait LanguageInteractionHandler {
    fn should_proceed(
        component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> bool;

    async fn create_ephemeral_response(
        ctx: &Context, component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> Result<()>;
}

struct IntroductionLanguageInteractionHandler;

struct RulesLanguageInteractionHandler;

fn find_language(component: &MessageComponentInteraction) -> Result<VoxeruSupportedLanguage> {
    let value = component.data.values.get(0)
        .context("No language was given.")?;
    for lang in VoxeruSupportedLanguage::values() {
        if lang.id() == value.as_str() {
            return Ok(lang);
        }
    }
    bail!("Invalid language: '{}'.", value)
}

async fn send_ephemeral<'a>(ctx: &Context, content: &'a str, component: &MessageComponentInteraction) -> Result<()> {
    component.create_interaction_response(ctx, |res| {
        res.kind(InteractionResponseType::ChannelMessageWithSource)
            .interaction_response_data(|data|
                data.content(content).flags(MessageFlags::EPHEMERAL))
    }).await?;
    Ok(())
}

#[async_trait]
impl LanguageInteractionHandler for IntroductionLanguageInteractionHandler {
    fn should_proceed(
        component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> bool {
        component.data.custom_id == "language" &&
            component.channel_id.0 == config.settings.guild.channel_id.introduction
    }

    async fn create_ephemeral_response(
        ctx: &Context, component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> Result<()> {
        let lang = find_language(component)?;
        let value = locale!(config, lang, topic.introduction.description);
        send_ephemeral(ctx, value, component).await?;
        Ok(())
    }
}

#[async_trait]
impl LanguageInteractionHandler for RulesLanguageInteractionHandler {
    fn should_proceed(
        component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> bool {
        component.data.custom_id == "language" &&
            component.channel_id.0 == config.settings.guild.channel_id.rules
    }

    async fn create_ephemeral_response(
        ctx: &Context, component: &MessageComponentInteraction, config: &VoxeruConfig,
    ) -> Result<()> {
        let lang = find_language(component)?;
        let mut buffer = format!("{}\n\n", locale!(config, lang, topic.rules.header));
        for (index, rule) in locale!(config, lang, topic.rules.values).iter().enumerate() {
            buffer.push_str(&format!("{}. 🔹 {}\n", index + 1, rule));
        }
        send_ephemeral(ctx, &buffer, component).await?;
        Ok(())
    }
}

pub async fn handle_interaction(
    context: &Context,
    interaction: &Interaction,
    config: &VoxeruConfig,
) -> Result<()> {
    match interaction {
        Interaction::MessageComponent(component) => {
            if IntroductionLanguageInteractionHandler::should_proceed(component, config) {
                IntroductionLanguageInteractionHandler::create_ephemeral_response(
                    context, component, config,
                ).await?;
            } else if RulesLanguageInteractionHandler::should_proceed(component, config) {
                RulesLanguageInteractionHandler::create_ephemeral_response(
                    context, component, config,
                ).await?;
            }
            Ok(())
        }
        _ => Ok(()),
    }
}
