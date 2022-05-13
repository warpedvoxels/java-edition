use anyhow::{Result, bail};
use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize, Debug, Clone, PartialEq, Eq)]
pub struct Username(String);

impl Username {
    pub const MAX_LENGTH: usize = 16;

    pub fn value(&self) -> Result<String> {
        if self.0.len() > Self::MAX_LENGTH {
            bail!("Username is too long (> 16)");
        }
        Ok(self.0.clone())
    }
}

impl From<String> for Username {
    fn from(value: String) -> Self {
        Self(value)
    }
}

impl From<&str> for Username {
    fn from(value: &str) -> Self {
        Self(value.to_string())
    }
}

impl ToString for Username {
    fn to_string(&self) -> String {
        self.value().unwrap()
    }
}

impl AsRef<String> for Username {
    fn as_ref(&self) -> &String {
        &self.0
    }
}

