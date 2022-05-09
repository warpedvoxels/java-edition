pub use crate::definitions::entity::{Player, PlayerTypeDef};
use crate::{app::SqlPool, definitions::returned::ReturnedPlayer};
use actix_web::Either;
use anyhow::{Context, Result};
use sea_query::{
    ColumnDef, Expr, OnConflict, Order, PostgresDriver, PostgresQueryBuilder, Query, Table,
};
use tokio_postgres::Row;
use uuid::Uuid;

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

impl From<Row> for Player {
    fn from(row: Row) -> Self {
        Self {
            uuid: row.get("uuid"),
            hexes: row.get("hexes"),
            last_username: row.get("last_username"),
            last_seen: row.get("last_seen"),
            created_at: row.get("created_at"),
            updated_at: row.get("updated_at"),
        }
    }
}

impl From<&Player> for ReturnedPlayer {
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
    fn columns() -> std::slice::Iter<'static, Self> {
        static COLUMNS: [PlayerTypeDef; 6] = [
            PlayerTypeDef::Uuid,
            PlayerTypeDef::Hexes,
            PlayerTypeDef::LastUsername,
            PlayerTypeDef::LastSeen,
            PlayerTypeDef::CreatedAt,
            PlayerTypeDef::UpdatedAt,
        ];
        COLUMNS.iter()
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

fn create_internally(entity: &Player) -> sea_query::InsertStatement {
    let mut statement = Query::insert();
    statement
        .into_table(PlayerTypeDef::Table)
        .columns(PlayerTypeDef::columns().copied().collect::<Vec<_>>())
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

#[async_trait::async_trait]
impl Entity<Player, Either<Uuid, String>, SqlPool> for Player {
    async fn up(pool: &SqlPool) -> Result<()> {
        let mut sql = Table::create();
        sql.table(PlayerTypeDef::Table).if_not_exists();
        for column in PlayerTypeDef::columns() {
            sql.col(&mut column.def());
        }
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        client
            .execute(&sql.build(PostgresQueryBuilder), &[])
            .await
            .context("Could not create the Player table.")?;
        Ok(())
    }

    async fn find(pool: &SqlPool, id: Either<Uuid, String>) -> Result<Option<Player>> {
        let expr = match id {
            Either::Left(uuid) => Expr::col(PlayerTypeDef::Uuid).eq(uuid),
            Either::Right(username) => Expr::col(PlayerTypeDef::LastUsername).eq(username),
        };
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns().copied().collect::<Vec<_>>())
            .from(PlayerTypeDef::Table)
            .limit(1)
            .and_where(expr)
            .build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        let row = client
            .query_one(&sql, &values.as_params())
            .await
            .context("Could not find the player.")?;
        Ok(Some(Player::from(row)))
    }

    async fn find_all(pool: &SqlPool) -> Result<Vec<Player>> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns().copied().collect::<Vec<_>>())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        let rows = client
            .query(&sql, &values.as_params())
            .await
            .context("Could not find all the players.")?
            .into_iter()
            .map(Player::from)
            .collect::<Vec<_>>();
        Ok(rows)
    }

    async fn create(&self, pool: &SqlPool) -> Result<()> {
        let (sql, values) = create_internally(self).build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        client
            .query_one(&sql, &values.as_params())
            .await
            .context("Failed to create the player.")?;
        Ok(())
    }

    async fn update(&self, pool: &SqlPool) -> Result<()> {
        let (sql, values) = create_internally(self)
            .on_conflict(
                OnConflict::column(PlayerTypeDef::Uuid)
                    .update_columns(PlayerTypeDef::columns().copied().collect::<Vec<_>>())
                    .to_owned(),
            )
            .build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        client
            .execute(&sql, &values.as_params())
            .await
            .context("Failed to update the player.")?;
        Ok(())
    }

    async fn find_all_with_offset(pool: &SqlPool, offset: u64, limit: u64) -> Result<Vec<Player>> {
        let (sql, values) = Query::select()
            .columns(PlayerTypeDef::columns().copied().collect::<Vec<_>>())
            .from(PlayerTypeDef::Table)
            .order_by(PlayerTypeDef::Uuid, Order::Asc)
            .limit(limit)
            .offset(offset)
            .build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        let rows = client
            .query(&sql, &values.as_params())
            .await
            .context("Could not find all the players.")?
            .into_iter()
            .map(Player::from)
            .collect::<Vec<_>>();
        Ok(rows)
    }

    async fn delete(pool: &SqlPool, id: Either<Uuid, String>) -> Result<()> {
        let expr = match id {
            Either::Left(uuid) => Expr::col(PlayerTypeDef::Uuid).eq(uuid),
            Either::Right(username) => Expr::col(PlayerTypeDef::LastUsername).eq(username),
        };
        let (sql, values) = Query::delete()
            .from_table(PlayerTypeDef::Table)
            .and_where(expr)
            .build(PostgresQueryBuilder);
        let client = pool
            .get()
            .await
            .context("Could not get client from pool.")?;
        client
            .execute(&sql, &values.as_params())
            .await
            .context("Failed to delete the player.")?;
        Ok(())
    }
}
