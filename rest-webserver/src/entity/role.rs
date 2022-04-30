pub use crate::definitions::{entity::Role, rest::RestRole};

impl From<&Role> for RestRole {
    fn from(role: &Role) -> Self {
        Self {
            id: role.id.clone(),
            unicode_character: role.unicode_character.clone(),
            color: role.color.clone(),
            tab_list_index: role.tab_list_index.clone(),
        }
    }
}

