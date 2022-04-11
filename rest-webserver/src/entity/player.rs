use crate::app::WebserverStateData;
use async_trait::async_trait;
use chrono::NaiveDateTime;
use sea_query::{gen_type_def, ColumnDef, Expr, Order, PostgresQueryBuilder, Query, Table};
use sqlx::postgres::PgQueryResult;
use uuid::Uuid;

sea_query::sea_query_driver_postgres!();
use crate::util::ColumnsDef;
use sea_query_driver_postgres::{bind_query_as};

use super::Entity;

#[derive(sqlx::FromRow, Debug)]
#[gen_type_def]
pub struct Player {
    pub uuid: Uuid,
    pub hexes: u32,
    pub last_username: String,
    pub last_seen: NaiveDateTime,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

impl ColumnsDef<PlayerTypeDef> for PlayerTypeDef {
    fn columns() -> Vec<PlayerTypeDef> {
        vec![
            PlayerTypeDef::Uuid,
            PlayerTypeDef::Hexes,
            PlayerTypeDef::LastUsername,
            PlayerTypeDef::LastSeen,
            PlayerTypeDef::CreatedAt,
            PlayerTypeDef::UpdatedAt,
        ]
    }
    fn def(&self) -> ColumnDef {
        let mut column = ColumnDef::new(*self);
        match *self {
            PlayerTypeDef::Uuid => column.uuid().not_null().primary_key(),
            PlayerTypeDef::Hexes => column.integer().not_null().default(0),
            PlayerTypeDef::LastUsername => column.string_len(16),
            PlayerTypeDef::LastSeen => column.date_time(),
            PlayerTypeDef::CreatedAt => column.date_time(),
            PlayerTypeDef::UpdatedAt => column.date_time(),
            PlayerTypeDef::Table => unreachable!(),
        };
        column
    }
}

#[async_trait]
impl Entity<Player, Uuid> for Player {
    async fn up(state: &crate::app::WebserverStateData) -> Result<PgQueryResult, sqlx::Error> {
        let mut sql = Table::create();
        sql.table(PlayerTypeDef::Table).if_not_exists();
        for column in PlayerTypeDef::columns() {
            sql.col(&mut column.def());
        }
        sqlx::query(&sql.build(PostgresQueryBuilder))
            .execute(&state.pool)
            .await
    }

    async fn find(state: &WebserverStateData, id: Uuid) -> Option<Player> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .limit(1)
            .and_where(Expr::col(PlayerTypeDef::Uuid).eq(id))
            .build(PostgresQueryBuilder);
        let player = bind_query_as(sqlx::query_as::<_, Player>(&sql), &values)
            .fetch_one(&state.pool)
            .await;
        player.ok()
    }

    async fn find_all(state: &WebserverStateData) -> Vec<Player> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .build(PostgresQueryBuilder);
        let players = bind_query_as(sqlx::query_as::<_, Player>(&sql), &values)
            .fetch_all(&state.pool)
            .await;
        if players.is_err() {
            log::error!(
                "An error occurred while fetching players: {}",
                players.err().unwrap()
            );
            return vec![];
        }
        players.ok().unwrap()
    }
}
