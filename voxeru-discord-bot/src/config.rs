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

use std::fs::File;
use std::io::Read;
use std::path::Path;

use anyhow::{Context, Result};
use serde::Deserialize;
use structstruck::strike;

#[derive(Deserialize, Debug)]
pub(crate) struct VoxeruEnvironmentVariables {
    #[serde(rename = "DISCORD_AUTH_TOKEN")]
    pub(crate) discord_auth_token: String,
}

strike!(#[strikethrough[derive(Deserialize, Debug)]]
pub struct VoxeruSettings {
    pub accent_color: u32,
    pub command_prefix: String,
    pub locale_dir: std::path::PathBuf,
    pub asset_path: struct VoxeruAssetSettings {
        pub header: struct VoxeruHeaderAssetSettings {
            pub introduction: std::path::PathBuf,
            pub rules: std::path::PathBuf,
        }
    },
    pub guild: struct VoxeruGuildSettings {
        pub id: u64,
        pub channel_id: struct VoxeruGuildChannelIdSettings {
            pub introduction: u64,
            pub rules: u64,
            pub general: u64,
            pub commands: u64,
            pub support: u64,
            pub new_members: u64,
            pub announcements: u64,
        }
    }
});

strike!(#[strikethrough[derive(Deserialize, Debug)]]
pub struct VoxeruLocalisation {
    pub english: struct VoxeruLocalisationInner {
        pub commands: struct CommandsLocalisation {
            pub source: struct LinksCommandLocalisation {
                pub header: String,
                pub footer: String,
            },
            pub invite: struct InviteCommandLocalisation {
                pub header: String,
                pub footer: String,
            }
        },
        pub description: struct DescriptionLocalisation {
            pub welcome_screen: String,
            pub introduction: String,
            pub rules: String,
            pub general: String,
            pub support: String,
        },
        pub topic: struct TopicLocalisation {
            pub internalisation: String,
            pub introduction: struct IntroductionTopicLocalisation {
                pub title: String,
                pub description: String,
            },
            pub rules: struct RulesTopicLocalisation {
                pub title: String,
                pub header: String,
                pub values: Vec<String>
            },
        }
    },
    pub portuguese: VoxeruLocalisationInner,
    pub spanish: VoxeruLocalisationInner,
    pub german: VoxeruLocalisationInner
});

macro_rules! supported_languages {
    ($([$name:ident, $lowercase:ident, $source:literal, $emoji:literal]),*) => {
        #[derive(poise::ChoiceParameter)]
        pub enum VoxeruSupportedLanguage {
            $(#[name = $source] $name),*
        }

        impl VoxeruSupportedLanguage {
            pub fn values() -> Vec<Self> {
                vec![$(Self::$name),*]
            }
            pub fn source_name(&self) -> String {
                String::from(match self {
                    $(Self::$name => $source),*
                })
            }
            pub fn emoji(&self) -> &'static str {
                match self {
                    $(Self::$name => $emoji),*
                }
            }
            pub fn id(&self) -> &'static str {
                match self {
                    $(Self::$name => stringify!($lowercase)),*
                }
            }
        }
    }
}

supported_languages! { [English, english, "British English", "🇬🇧"],
                       [Portuguese, portuguese, "Português Brasileiro", "🇧🇷"],
                       [Spanish, spanish, "Español Europeo", "🇪🇸"],
                       [German, german, "Deutsch", "🇩🇪"] }

#[macro_export]
macro_rules! locale {
    ($config:expr, $language:expr, $($notation:ident).*) => {
        {
            use $crate::config::VoxeruSupportedLanguage;
            let locale = &$config.locale;
            let lang = match $language {
                VoxeruSupportedLanguage::English => &locale.english,
                VoxeruSupportedLanguage::Portuguese => &locale.portuguese,
                VoxeruSupportedLanguage::Spanish => &locale.spanish,
                VoxeruSupportedLanguage::German => &locale.german,
            };
            &lang.$($notation).*
        }
    }
}

impl Default for VoxeruSupportedLanguage {
    fn default() -> Self {
        Self::English
    }
}

pub struct VoxeruConfig {
    pub(crate) env: VoxeruEnvironmentVariables,
    pub settings: VoxeruSettings,
    pub locale: VoxeruLocalisation,
}

impl VoxeruSettings {
    pub fn load() -> Result<VoxeruSettings> {
        let mut config_file = if Path::new("settings.json").exists() {
            File::open("settings.json")?
        } else {
            let dir = dirs::config_dir().context("Failed to get config directory.")?;
            let path = dir.join("voxeru/settings.json");
            File::open(&path).with_context(|| format!("Failed to open {}.", path.display()))?
        };
        let mut buffer = String::new();
        config_file.read_to_string(&mut buffer)?;
        serde_json::from_str(&buffer).context("Failed to parse JSON from settings file.")
    }
}

impl VoxeruLocalisation {
    pub fn load(directory: &Path, language: VoxeruSupportedLanguage) -> Result<VoxeruLocalisationInner> {
        let mut file = File::open(directory.join(format!("{}.json", language.id())))
            .context(format!("Failed to open localisation file for {}.", language.source_name()))?;
        let mut buffer = String::new();
        file.read_to_string(&mut buffer)?;
        serde_json::from_str(&buffer).context("Failed to parse JSON from localisation file.")
    }
}

pub async fn parse() -> Result<VoxeruConfig> {
    let discord_auth_token = std::env::var("DISCORD_AUTH_TOKEN")
        .context("Failed to read environment variable DISCORD_AUTH_TOKEN.")?;
    let settings = VoxeruSettings::load()?;
    Ok(VoxeruConfig {
        locale: VoxeruLocalisation {
            english: VoxeruLocalisation::load(&settings.locale_dir, VoxeruSupportedLanguage::English)?,
            portuguese: VoxeruLocalisation::load(&settings.locale_dir, VoxeruSupportedLanguage::Portuguese)?,
            spanish: VoxeruLocalisation::load(&settings.locale_dir, VoxeruSupportedLanguage::Spanish)?,
            german: VoxeruLocalisation::load(&settings.locale_dir, VoxeruSupportedLanguage::German)?,
        },
        settings,
        env: VoxeruEnvironmentVariables {
            discord_auth_token,
        },
    })
}
