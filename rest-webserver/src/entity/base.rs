use sqlx::postgres::PgQueryResult;
use async_trait::async_trait;
use crate::app::WebserverState;

#[async_trait]
pub trait Entity<T, I> where T: Entity<T, I> {
    async fn up(state: &WebserverState) -> Result<PgQueryResult, sqlx::Error>;

    async fn find(state: &WebserverState, id: I) -> Option<T>;

    async fn find_all(state: &WebserverState) -> Vec<T>;
}