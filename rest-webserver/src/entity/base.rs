use crate::app::WebserverStateData;
use async_trait::async_trait;
use sea_query::ColumnDef;
use sqlx::postgres::PgQueryResult;

#[async_trait]
pub trait Entity<T, I>
where
    T: Entity<T, I>,
{
    async fn up(state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error>;

    async fn find(state: &WebserverStateData, id: I) -> Option<T>;

    async fn find_all(state: &WebserverStateData) -> Vec<T>;

    async fn find_all_with_offset(state: &WebserverStateData, offset: u64, limit: u64) -> Vec<T>;

    async fn create(&self, state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error>;

    async fn update(&self, state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error>;

    async fn delete(state: &WebserverStateData, id: I) -> Result<PgQueryResult, sqlx::Error>;
}

pub trait ColumnsDef<T> {
    fn columns() -> Vec<T>;

    fn def(&self) -> ColumnDef;
}
