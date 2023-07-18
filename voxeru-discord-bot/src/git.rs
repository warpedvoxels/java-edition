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

use std::fmt::Write;

use anyhow::{bail, Context, Result};
use poise::async_trait;
use regex::Regex;
use reqwest::Client as HttpClient;

/// The contents of a file. Results after the process of matching URL via regular
/// expressions, getting their raw text path, and requesting their body.
pub struct FileContents {
    /// The name of the file.
    pub file_name: String,
    /// The line range of the file, if any.
    pub line_range: Option<(usize, usize)>,
    /// The raw text of the file.
    pub value: String,
}

impl FileContents {
    // /// Retrieves the extension of the file, if any. Useful for syntax highlighting
    // /// in Discord code blocks.
    // pub fn extension(&self) -> Option<&str> {
    //     let last_dot = self.file_name.rfind('.')?;
    //     Some(&self.file_name[last_dot + 1..])
    // }

    /// Returns whether the file contents are empty.
    pub fn is_empty(&self) -> bool {
        self.value.is_empty()
    }
}

/// The result of parsing the URL of a file in a Git repository of popular hosting
/// services, via regular expressions.
pub struct GitFileUrlParsingResult {
    line_range: Option<(usize, usize)>,
    username: String,
    repository: String,
    branch_then_file: String,
}

#[async_trait]
pub trait GitHostingService {
    /// Parses the URL of a file in a Git repository within a popular hosting service,
    /// via regular expressions.
    fn parse_url(&self, input: &str) -> Result<GitFileUrlParsingResult>;

    /// Gets the raw text of a file in a Git repository within a popular Git
    /// repository hosting service.
    async fn get_file_contents(&self, parsing: &GitFileUrlParsingResult) -> Result<FileContents>;
}

/// Implementation of the `GitHostingService` trait for GitHub.
/// - `regular_expression`: The regular expression used to match GitHub file URLs.
/// - `http_client`: The HTTP client used to request the raw text of a file.
/// - `max_read_size`: The maximum size of the file to be read, in bytes.
pub struct GitHub {
    /// The regular expression used to match GitHub file URLs.
    regular_expression: Regex,
    /// The HTTP client used to request the raw text of a file.
    http_client: HttpClient,
    /// The maximum size of the file to be read, in bytes.
    max_read_size: usize,
}

#[async_trait]
impl GitHostingService for GitHub {
    fn parse_url(&self, input: &str) -> Result<GitFileUrlParsingResult> {
        let matches = self
            .regular_expression
            .captures(input)
            .context("Failed to capture GitHub file URL.")?;
        let username = &matches[1];
        let repository = &matches[2];
        let branch_then_file = &matches[3];
        let line_range = if let Some(line_range_start) = &matches.get(5) {
            let line_range_start = line_range_start.as_str().parse::<u64>().context(
                "Failed to parse GitHub file URL line range start into an unsigned 64-bit integer.",
            )?;
            if let Some(line_range_end) = &matches.get(6) {
                let line_range_end = line_range_end.as_str().parse::<u64>()
                    .context("Failed to parse GitHub file URL line range end into an unsigned 64-bit integer.")?;
                if line_range_start < line_range_end {
                    Some((line_range_start as usize, line_range_end as usize))
                } else {
                    Some((line_range_start as usize, line_range_start as usize))
                }
            } else {
                None
            }
        } else {
            None
        };
        Ok(GitFileUrlParsingResult {
            line_range,
            username: username.to_string(),
            repository: repository.to_string(),
            branch_then_file: branch_then_file.to_string(),
        })
    }

    async fn get_file_contents(&self, parsing: &GitFileUrlParsingResult) -> Result<FileContents> {
        let raw_url = self.build_raw_url(parsing)?;
        log::info!("Requesting file contents from GitHub: {}", raw_url);
        let response = self
            .http_client
            .get(&raw_url)
            .send()
            .await
            .context("Failed to request file contents from GitHub.")?;
        if !response.status().is_success() {
            bail!(
                "Failed to request file contents from GitHub: {}",
                response.status()
            );
        }
        if let Some(content_length) = response.content_length() {
            if content_length > self.max_read_size as u64 {
                bail!(
                    "File contents from GitHub are too large: {} bytes.",
                    content_length
                );
            }
        }
        let last_slash_index = parsing
            .branch_then_file
            .rfind('/')
            .context("Failed to get last slash index from GitHub file URL.")?;
        let file_name = &parsing.branch_then_file[last_slash_index + 1..];
        let value = response
            .text()
            .await
            .context("Failed to read file contents from GitHub.")?;
        let (value, line_range) = if let Some(line_range) = parsing.line_range {
            let (start, end) = line_range;
            let start = if start > value.len() {
                value.len()
            } else {
                start
            };
            let end = if end > value.len() { value.len() } else { end };
            let end_ = if end - start == 0 { 1 } else { end - start };
            let value = value
                .lines()
                .skip(start)
                .take(end_)
                .collect::<Vec<_>>()
                .join("\n");
            (value, Some((start, end)))
        } else {
            (value, None)
        };
        Ok(FileContents {
            file_name: file_name.to_string(),
            line_range,
            value,
        })
    }
}

impl GitHub {
    /// Creates a new instance of the GitHub hosting service..
    /// - `max_read_size`: The maximum size of the file to be read, in bytes.
    pub fn new(max_read_size: usize) -> Result<Self> {
        let regular_expression = Regex::new(
            r"https?://(?:www\.)?github\.com/([\w\-]{1,39})/([\w\-.]{1,40})/blob/([^ #]+)(#L(\d+)-(\d+)?)?"
        ).context("Failed to build GitHub file URL regular expression.")?;
        let http_client = HttpClient::new();
        Ok(Self {
            regular_expression,
            http_client,
            max_read_size,
        })
    }

    /// Builds a raw file URL from parsing results.
    pub fn build_raw_url(&self, parsing: &GitFileUrlParsingResult) -> Result<String> {
        let mut raw_url = String::from("https://raw.githubusercontent.com/");
        write!(
            raw_url,
            "{}/{}/{}",
            parsing.username, parsing.repository, parsing.branch_then_file
        )
        .context("Failed to write to URL buffer.")?;
        Ok(raw_url)
    }
}

impl Default for GitHub {
    fn default() -> Self {
        Self::new(5 * 1024 * 1024).unwrap()
    }
}

#[test]
fn invalid_file_url_test() {
    let url = "https://github.com/warpedvoxels/discord-bot/main/src/main.rs";
    assert!(GitHub::default().parse_url(url).is_err());
}

#[test]
pub fn valid_github_file_url() {
    let input = "https://github.com/warpedvoxels/discord-bot/blob/main/src/main.rs";
    let expected = "https://raw.githubusercontent.com/warpedvoxels/discord-bot/main/src/main.rs";
    let github = GitHub::default();
    let parsing = github.parse_url(input).unwrap();
    assert_eq!(github.build_raw_url(&parsing).unwrap(), expected);
}
