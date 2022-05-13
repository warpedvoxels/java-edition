use std::collections::HashMap;

use anyhow::Result;
use hexalite_common::ExportFields;
use phf::Map;

use crate::app::HexaliteGrpcServer;
use std::fmt::Write;

/// An trait that defines the functionality for a CRUD SQL table.
/// The [I] generic is the type of the primary key.
#[async_trait::async_trait]
pub trait SqlxCrudComposing<I>
where
    Self: ExportFields + Sized,
{
    fn composing_context(&self) -> SqlEntityComposingContext;

    async fn up(ctx: &HexaliteGrpcServer) -> Result<()>;

    async fn publish(&self, ctx: &HexaliteGrpcServer) -> Result<()>;

    async fn delete(id: I, ctx: &HexaliteGrpcServer) -> Result<()>;

    async fn retrieve(id: I, ctx: &HexaliteGrpcServer) -> Result<Option<Self>>;

    async fn list(
        offset: Option<u32>,
        limit: Option<u32>,
        ctx: &HexaliteGrpcServer,
    ) -> Result<Vec<Self>>;
}

pub struct SqlRelation {
    pub name: String,
    pub id_type: &'static str,
}

pub struct SqlEntityComposingContext {
    pub id_type: String,
    pub name: String,
    /// All fields except by relations.
    pub fields: HashMap<String, String>,
}

impl SqlEntityComposingContext {
    fn new(name: String, fields: &Map<&'static str, &'static str>) -> Self {
        let id_type = fields
            .get("id")
            .unwrap_or_else(|| fields.get("uuid").expect("Expected an ID or UUID field"))
            .to_string();
        let mut fields = fields
            .into_iter()
            .map(|(k, v)| (k.to_string(), v.to_string()))
            .collect::<HashMap<String, String>>();
        fields.remove("id");
        fields.remove("uuid");
        Self {
            name,
            id_type,
            fields,
        }
    }
}

pub type DataTypeMap = HashMap<&'static str, String>;

macro_rules! data_types {
    ($($kind:ty => $name:expr),* $(,)?) => {
        {
            let mut map = DataTypeMap::new();
            $(
                map.insert(
                    std::any::type_name::<$kind>(),
                    $name.to_string(),
                );
            )*
            map
        }
    }
}

lazy_static::lazy_static! {
    pub static ref DATA_TYPES: DataTypeMap = data_types! {
        String => "TEXT",
        
    };
}

/// Build a SQL statement of a table creation of the entity itself and all of its related entities.
pub fn build_create_table_sql<T>(composing: &T) -> Result<String>
where
    T: SqlxCrudComposing<T>,
{
    let ctx = composing.composing_context();
    let mut buf = String::new();

    writeln!(buf, "CREATE TABLE IF NOT EXISTS {} (", ctx.name).unwrap();
    for (name, kind) in ctx.fields.iter() {
        writeln!(buf, "    {name}").unwrap();
    }
    Ok(buf)
}
