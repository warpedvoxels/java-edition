pub trait ColumnsDef<T>
where
    T: sea_query::Iden,
{
    fn columns() -> Vec<T>;

    fn def(&self) -> sea_query::ColumnDef;
}

#[async_trait::async_trait]
pub trait Entity<T, I, S, R, E>
where
    T: Entity<T, I, S, R, E>,
{
    async fn up(state: &S) -> Result<R, E>;

    async fn find(state: &S, id: I) -> Option<T>;

    async fn find_all(state: &S) -> Vec<T>;

    async fn find_all_with_offset(state: &S, offset: u64, limit: u64) -> Vec<T>;

    async fn create(&self, state: &S) -> Result<R, E>;

    async fn update(&self, state: &S) -> Result<R, E>;

    async fn delete(state: &S, id: I) -> Result<R, E>;
}
