use std::collections::HashMap;

use anyhow::{Context, Result};
use heck::ToSnakeCase;
use hexalite_common::ExportFields;
use phf::Map;

use crate::app::HexaliteGrpcServer;
use crate::datatype::*;
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
    ($($kind:ty => $name:tt),* $(,)?) => {
        {
            let mut map: DataTypeMap = HashMap::new();
            $(
                map.insert(std::any::type_name::<$kind>(), format!("{} NOT NULL", $name));
                map.insert(std::any::type_name::<Option<$kind>>(), $name.to_string());
            )*
            map
        }
    }
}


lazy_static::lazy_static! {
    pub static ref DATA_TYPES: DataTypeMap = data_types! {
        String => "TEXT",
        Username => "VARCHAR(16)",
        uuid::Uuid => "UUID",
        u16 => "SMALLSERIAL",
        i16 => "SMALLINT",
        u32 => "SERIAL",
        u64 => "BIGSERIAL",
        i32 => "INTEGER",
        i64 => "BIGINT",
        usize => "BIGSERIAL",
        isize => "SERIAL",
        f32 => "REAL",
        f64 => "DOUBLE PRECISION",
        bool => "BOOLEAN",
        chrono::NaiveDateTime => "TIMESTAMP DEFAULT NOW()",
        serde_json::value::Value => "JSONB",
    };
}

/// Build a SQL statement of a table creation of the entity itself and all of its related entities.
pub fn build_create_table_sql<T>(composing: &T) -> Result<String>
where
    T: SqlxCrudComposing<T>,
{
    fn write_column(
        name: &str,
        kind: &str,
        table_name: &str,
        buf: &mut String,
    ) -> Result<()> {
        let kind = DATA_TYPES.get(kind).with_context(|| {
            format!(
                "Could not create the table '{}'. Unknown data type: {}",
                table_name, kind
            )
        })?;
        write!(buf, "{name} {kind}").unwrap();
        if name == "id" {
            buf.push_str(" UNIQUE PRIMARY KEY");
        }
        Ok(())
    }

    let ctx = composing.composing_context();
    let table_name = ctx.name.to_snake_case();
    let mut buf = String::new();

    writeln!(buf, "CREATE TABLE IF NOT EXISTS {} (", table_name).unwrap();
    write_column("id", &ctx.id_type, &table_name, &mut buf)?;
    for (name, kind) in &ctx.fields {
        write_column(name, kind, &table_name, &mut buf)?;
    }
    buf.push_str("\n)");

    Ok(buf)
}
