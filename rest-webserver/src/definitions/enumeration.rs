use super::protocol::{CommunicationsKey, RedisKey};

impl ToString for RedisKey {
    fn to_string(&self) -> String {
        String::from(match *self {
            RedisKey::InternalIdentity => "INTERNAL_IDENTITY",
        })
    }
}

impl ToString for CommunicationsKey {
    fn to_string(&self) -> String {
        String::from(match *self {
            CommunicationsKey::DataQueue => "DATA_QUEUE",
        })
    }
}

impl CommunicationsKey {
    pub fn values() -> std::slice::Iter<'static, Self> {
        static VALUES: [CommunicationsKey; 1] = [CommunicationsKey::DataQueue];
        VALUES.iter()
    }
}
