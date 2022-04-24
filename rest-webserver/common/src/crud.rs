use sqlx::postgres::PgQueryResult;
use sea_query::ColumnDef;
use async_trait::async_trait;

#[async_trait]
pub trait Entity<T, I, S>
where
    T: Entity<T, I, S>,
{
    async fn up(state: &S) -> Result<PgQueryResult, sqlx::Error>;

    async fn find(state: &S, id: I) -> Option<T>;

    async fn find_all(state: &S) -> Vec<T>;

    async fn find_all_with_offset(state: &S, offset: u64, limit: u64) -> Vec<T>;

    async fn create(&self, state: &S) -> Result<PgQueryResult, sqlx::Error>;

    async fn update(&self, state: &S) -> Result<PgQueryResult, sqlx::Error>;

    async fn delete(state: &S, id: I) -> Result<PgQueryResult, sqlx::Error>;
}

pub trait ColumnsDef<T> {
    fn columns() -> Vec<T>;

    fn def(&self) -> ColumnDef;
}
