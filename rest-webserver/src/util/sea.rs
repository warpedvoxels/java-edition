use sea_query::ColumnDef;

pub trait ColumnsDef<T> {
    fn columns() -> Vec<T>;

    fn def(&self) -> ColumnDef;
}