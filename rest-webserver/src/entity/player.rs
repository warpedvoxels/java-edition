use crate::{app::{SqlPool, SqlQueryResult}, definitions::rest::RestPlayer};
pub use crate::definitions::entity::{Player, PlayerTypeDef};
use actix_web::Either;
use sea_query::{ColumnDef, Expr, OnConflict, Order, PostgresQueryBuilder, Query, Table};
use uuid::Uuid;

sea_query::sea_query_driver_postgres!();

use self::sea_query_driver_postgres::bind_query;
use sea_query_driver_postgres::bind_query_as;

use super::{ColumnsDef, Entity};

impl Default for Player {
    fn default() -> Self {
        Self {
            uuid: Uuid::new_v4(),
            hexes: 0,
            last_username: "".to_string(),
            last_seen: chrono::Utc::now(),
            created_at: chrono::Utc::now(),
            updated_at: chrono::Utc::now(),
        }
    }
}

impl From<&Player> for RestPlayer {
    fn from(player: &Player) -> Self {
        Self {
            uuid: player.uuid,
            hexes: player.hexes,
            last_username: player.last_username.clone(),
            last_seen: player.last_seen,
            created_at: player.created_at,
            updated_at: player.updated_at,
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
            PlayerTypeDef::LastUsername => column.string_len(16).not_null(),
            PlayerTypeDef::LastSeen => column
                .date_time()
                .not_null()
                .extra("DEFAULT NOW()".to_string()),
            PlayerTypeDef::CreatedAt => column
                .date_time()
                .not_null()
                .extra("DEFAULT NOW()".to_string()),
            PlayerTypeDef::UpdatedAt => column
                .date_time()
                .not_null()
                .extra("DEFAULT NOW()".to_string()),
            _ => unreachable!(),
        };
        column
    }
}

#[async_trait::async_trait]
impl Entity<Player, Either<Uuid, String>, SqlPool, SqlQueryResult, sqlx::Error> for Player {
    async fn up(pool: &SqlPool) -> Result<SqlQueryResult, sqlx::Error> {
        let mut sql = Table::create();
        sql.table(PlayerTypeDef::Table).if_not_exists();
        for column in PlayerTypeDef::columns() {
            sql.col(&mut column.def());
        }
        sqlx::query(&sql.build(PostgresQueryBuilder))
            .execute(pool)
            .await
    }

    async fn find(pool: &SqlPool, id: Either<Uuid, String>) -> Option<Player> {
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
            .fetch_one(pool)
            .await;
        player.ok()
    }

    async fn find_all(pool: &SqlPool) -> Vec<Player> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .build(PostgresQueryBuilder);
        let players = bind_query_as(sqlx::query_as::<_, Player>(&sql), &values)
            .fetch_all(pool)
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

    async fn create(&self, pool: &SqlPool) -> Result<SqlQueryResult, sqlx::Error> {
        let (sql, values) = create_internally(self).build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values).execute(pool).await
    }

    async fn update(&self, pool: &SqlPool) -> Result<SqlQueryResult, sqlx::Error> {
        let (sql, values) = create_internally(self)
            .on_conflict(
                OnConflict::column(PlayerTypeDef::Uuid)
                    .update_columns(PlayerTypeDef::columns())
                    .to_owned(),
            )
            .build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values).execute(pool).await
    }

    async fn find_all_with_offset(pool: &SqlPool, offset: u64, limit: u64) -> Vec<Player> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .limit(limit)
            .offset(offset)
            .build(PostgresQueryBuilder);
        let players = bind_query_as(sqlx::query_as::<_, Player>(&sql), &values)
            .fetch_all(pool)
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
        pool: &SqlPool,
        id: Either<Uuid, String>,
    ) -> Result<SqlQueryResult, sqlx::Error> {
        let expr = match id {
            Either::Left(uuid) => Expr::col(PlayerTypeDef::Uuid).eq(uuid),
            Either::Right(username) => Expr::col(PlayerTypeDef::LastUsername).eq(username),
        };
        let (sql, values) = Query::delete()
            .from_table(PlayerTypeDef::Table)
            .and_where(expr)
            .build(PostgresQueryBuilder);
        bind_query(sqlx::query(&sql), &values).execute(pool).await
    }
}

fn create_internally(entity: &Player) -> sea_query::InsertStatement {
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
