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

#[test]
pub fn test_username() {
    let username = Username::from("my_username");
    assert_eq!(username.value().unwrap(), "my_username");
    assert!(Username::from("my_username_is_too_long_aaaa").value().is_err());

    let serialized = serde_json::to_string(&username).unwrap(); 
    assert_eq!(serialized, r#""my_username""#);
}
