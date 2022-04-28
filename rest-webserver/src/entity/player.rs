use crate::{app::WebserverStateData, ColumnsDef, Entity};
use actix_web::Either;
use async_trait::async_trait;
use chrono::{DateTime, Utc};
use sea_query::{
    gen_type_def, ColumnDef, Expr, OnConflict, Order, PostgresQueryBuilder, Query, Table,
};
use sqlx::postgres::PgQueryResult;
use uuid::Uuid;

sea_query::sea_query_driver_postgres!();

use self::sea_query_driver_postgres::bind_query;
use sea_query_driver_postgres::bind_query_as;

#[derive(sqlx::FromRow, Debug, Clone)]
#[gen_type_def]
pub struct Player {
    pub uuid: Uuid,
    pub hexes: u32,
    pub last_username: String,
    pub last_seen: DateTime<Utc>,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

impl Default for Player {
    fn default() -> Self {
        Self {
            uuid: Uuid::new_v4(),
            hexes: 0,
            last_username: String::from(""),
            last_seen: Utc::now(),
            created_at: Utc::now(),
            updated_at: Utc::now(),
        }
    }
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
            PlayerTypeDef::CreatedAt => column.date_time().extra("DEFAULT NOW()".to_string()),
            PlayerTypeDef::UpdatedAt => column.date_time().extra("DEFAULT NOW()".to_string()),
            PlayerTypeDef::Table => unreachable!(),
        };
        column
    }
}

#[async_trait]
impl Entity<Player, Either<Uuid, String>, WebserverStateData> for Player {
    async fn up(state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error> {
        let mut sql = Table::create();
        sql.table(PlayerTypeDef::Table).if_not_exists();
        for column in PlayerTypeDef::columns() {
            sql.col(&mut column.def());
        }
        sqlx::query(&sql.build(PostgresQueryBuilder))
            .execute(&state.pool)
            .await
    }

    async fn find(state: &WebserverStateData, id: Either<Uuid, String>) -> Option<Player> {
        let expr = match id {
            Either::Left(uuid) => Expr::col(PlayerTypeDef::Uuid).eq(uuid),
            Either::Right(username) => Expr::col(PlayerTypeDef::LastUsername).eq(username),
        };
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .limit(1)
            .and_where(expr)
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

    async fn create(&self, state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error> {
        let (sql, values) = create_internally(self, state).build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values)
            .execute(&state.pool)
            .await
    }

    async fn update(&self, state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error> {
        let (sql, values) = create_internally(self, state)
            .on_conflict(
                OnConflict::column(PlayerTypeDef::Uuid)
                    .update_columns(PlayerTypeDef::columns())
                    .to_owned(),
            )
            .build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values)
            .execute(&state.pool)
            .await
    }

    async fn find_all_with_offset(
        state: &WebserverStateData,
        offset: u64,
        limit: u64,
    ) -> Vec<Player> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .limit(limit)
            .offset(offset)
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

    async fn delete(
        state: &WebserverStateData,
        id: Either<Uuid, String>,
    ) -> Result<PgQueryResult, sqlx::Error> {
        let expr = match id {
            Either::Left(uuid) => Expr::col(PlayerTypeDef::Uuid).eq(uuid),
            Either::Right(username) => Expr::col(PlayerTypeDef::LastUsername).eq(username),
        };
        let (sql, values) = Query::delete()
            .from_table(PlayerTypeDef::Table)
            .and_where(expr)
            .build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values)
            .execute(&state.pool)
            .await
    }
}

fn create_internally(entity: &Player, _state: &WebserverStateData) -> sea_query::InsertStatement {
    let mut statement = Query::insert();
    statement
        .into_table(PlayerTypeDef::Table)
        .columns(PlayerTypeDef::columns())
        .values_panic(vec![
            entity.uuid.into(),
            entity.hexes.into(),
            entity.last_username.as_str().into(),
            entity.last_seen.into(),
            entity.created_at.into(),
            entity.updated_at.into(),
        ]);
    statement
}