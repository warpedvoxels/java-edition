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

#[macro_export]
macro_rules! framework {
    {
        token: $token:expr,
        commands: [$($command:expr),*],
        intents: [$($intent:ident),*],
        edit_tracker_duration: $edit_tracker_duration:expr,
        prefix: $prefix:expr,
        config: $config:expr,
        state: $state:expr,
    } => {
        {
            use poise::{EditTracker, Framework, FrameworkOptions, PrefixFrameworkOptions};
            use poise::serenity_prelude as serenity;
            use serenity::GatewayIntents;
            use std::time::Duration;
            use $crate::discord::event_handler::VoxeruEventHandler;

            Framework::builder()
                .token($token.clone())
                .intents(paste::paste! { $(GatewayIntents::[< $intent:upper >])|* })
                .options(FrameworkOptions {
                    commands: vec![$($command),*],
                    prefix_options: PrefixFrameworkOptions {
                        prefix: Some($prefix.clone()),
                        edit_tracker: Some(EditTracker::for_timespan($edit_tracker_duration)),
                        ..Default::default()
                    },
                    event_handler: move |ctx, event, framework, _data| {
                        Box::pin(async move {
                            if let Err(err) = VoxeruEventHandler::handle(ctx, event, framework).await {
                                log::error!("An error occurred while handling an event: {}", err);
                            }
                            Ok(())
                        })
                    },
                    ..Default::default()
                })
                .setup(move |ctx, ready, framework| {
                    Box::pin(async move {
                        on_ready(ready, &$config, ctx, framework).await?;
                        Ok($state())
                    })
                })
        }
    }
}
