use sqlx::postgres::PgQueryResult;

#[async_trait]
pub trait Entity<T, I, D>
where
    T: Entity<T, I>,
{
    async fn up(state: &D) -> Result<PgQueryResult, sqlx::Error>;

    async fn find(state: &D, id: I) -> Option<T>;

    async fn find_all(state: &D) -> Vec<T>;

    async fn find_all_with_offset(state: &D, offset: u64, limit: u64) -> Vec<T>;

    async fn create(&self, state: &D) -> Result<PgQueryResult, sqlx::Error>;

    async fn update(&self, state: &D) -> Result<PgQueryResult, sqlx::Error>;

    async fn delete(state: &D, id: I) -> Result<PgQueryResult, sqlx::Error>;
}

pub trait ColumnsDef<T> {
    fn columns() -> Vec<T>;

    fn def(&self) -> ColumnDef;
}
