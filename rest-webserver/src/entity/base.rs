use anyhow::Result;

pub trait ColumnsDef<T>
where
    T: sea_query::Iden,
{
    fn columns() -> Vec<T>;

    fn def(&self) -> sea_query::ColumnDef;
}

#[async_trait::async_trait]
pub trait Entity<T, I, S>
where
    T: Entity<T, I, S>,
{
    async fn up(state: &S) -> Result<()>;

    async fn find(state: &S, id: I) -> Result<Option<T>>;

    async fn find_all(state: &S) -> Result<Vec<T>>;

    async fn find_all_with_offset(state: &S, offset: u64, limit: u64) -> Result<Vec<T>>;

    async fn create(&self, state: &S) -> Result<()>;

    async fn update(&self, state: &S) -> Result<()>;

    async fn delete(state: &S, id: I) -> Result<()>;
}
