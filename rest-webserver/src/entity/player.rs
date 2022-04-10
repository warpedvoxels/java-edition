use chrono::NaiveDateTime;
use sea_query::{ColumnDef, Table, PostgresQueryBuilder, gen_type_def};
use uuid::Uuid;

use super::Entity;

#[gen_type_def]
pub struct Player {
    pub uuid: Uuid,
    pub hexes: u64,
    pub last_username: String,
    pub last_seen: NaiveDateTime,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}
