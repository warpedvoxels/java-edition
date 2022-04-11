use sqlx::postgres::PgQueryResult;
use async_trait::async_trait;
use crate::app::WebserverStateData;

#[async_trait]
pub trait Entity<T, I> where T: Entity<T, I> {
    async fn up(state: &WebserverStateData) -> Result<PgQueryResult, sqlx::Error>;

    async fn find(state: &WebserverStateData, id: I) -> Option<T>;

    async fn find_all(state: &WebserverStateData) -> Vec<T>;
}