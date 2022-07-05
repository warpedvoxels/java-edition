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

pub mod datatype {
    include!("datatype.rs");

    impl From<id::Data> for Id {
        fn from(data: id::Data) -> Id {
            Id { data: Some(data) }
        }
    }
}
