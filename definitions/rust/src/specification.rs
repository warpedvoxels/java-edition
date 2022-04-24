//! # Specification
//!
//! An `serde`-based implementation of the entity generation specification.
//! 

use std::collections::HashMap;

use serde::Deserialize;

#[derive(Debug, Clone, Deserialize)]
pub struct SpecificationRoot {
    pub name: String,
    pub location: RustAndKotlinType,
    pub feature_flag: String,
    pub fields: HashMap<String, SpecificationField>
}

#[derive(Debug, Clone, Deserialize)]
pub struct RustAndKotlinType {
    pub rust: String,
    pub kotlin: String
}

#[derive(Debug, Clone, Deserialize)]
pub struct SpecificationField {
    pub kind: RustAndKotlinType,
    #[serde(default)]
    pub sql: Option<String>,
}
