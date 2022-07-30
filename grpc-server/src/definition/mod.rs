pub mod entity;
pub mod protocol;
pub mod rest;

pub mod arcade {
    pub mod ffa {
        pub mod entity {
            include!("arcade.ffa.entity.rs");
        }
        pub mod protocol {
            include!("arcade.ffa.protocol.rs");
        }
    }
}
