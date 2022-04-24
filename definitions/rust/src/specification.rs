//! # Specification
//!
//! An `serde`-based implementation of the entity generation specification.
//! 

use std::collections::HashMap;

use serde::Deserialize;

#[derive(Debug, Clone, Deserialize)]
pub struct SpecificationRoot {
    pub entities: Vec<SpecificationEntity>
}

#[derive(Debug, Clone, Deserialize)]
pub struct SpecificationEntity {
    pub name: String,
    #[serde(rename = "package")]
    pub kotlin_package: String,
    pub fields: HashMap<String, SpecificationField>
}

#[derive(Debug, Clone, Deserialize)]
pub struct SpecificationField {
    pub rust: String,
    pub kotlin: String
}
