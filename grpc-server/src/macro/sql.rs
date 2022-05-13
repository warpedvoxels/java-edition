use std::collections::HashMap;

use anyhow::{Context, Result};
use heck::ToSnakeCase;
use hexalite_common::ExportFields;
use grpc_server_common::datatype::*;
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

pub struct SqlEntityComposingContext {
    pub id_type: Option<String>,
    pub name: String,
    pub fields: HashMap<String, String>,
}

pub type DataTypeMap = HashMap<&'static str, String>;
pub type Fields = Map<&'static str, &'static str>;

macro_rules! as_ctx_map {
    ($name:ident, $exclude_id:expr) => {
        {
            let fields = $name::fields();
            let id_type = fields
                .get("id")
                .unwrap_or_else(|| fields.get("uuid"))
                .map(|x| x.to_string());
            let mut fields = fields
                .into_iter()
                .map(|(k, v)| (k.to_string(), v.to_string()))
                .collect::<HashMap<String, String>>();
            let name = stringify!($name).to_string();
            if $exclude_id {
                fields.remove("id");
                fields.remove("uuid");
            }
            SqlEntityComposingContext {
                id_type,
                name,
                fields,
            }
        }
    };
}

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
fn build_create_table_sql(ctx: SqlEntityComposingContext) -> Result<String> {
    fn write_column(name: &str, kind: &str, table_name: &str, buf: &mut String) -> Result<()> {
        let kind = DATA_TYPES.get(kind).with_context(|| {
            format!(
                "Could not create the table '{}'. Unknown data type: {}",
                table_name, kind
            )
        })?;
        write!(buf, "\n{name} {kind}").unwrap();
        if name == "id" {
            if !kind.starts_with("core::option::Option<") {
                buf.push_str(" UNIQUE");
            }
            buf.push_str(" PRIMARY KEY");
        }
        if let Some(related) = name.strip_suffix("_id") {
            write!(buf, ", FOREIGN KEY ({name}) REFERENCES {related}(id)")
                .with_context(|| format!("Failed to write foreign key '{related}' to buffer."))?;
        }
        buf.push_str(",\n");
        Ok(())
    }
    let table_name = ctx.name.to_snake_case();
    let mut buf = String::new();

    writeln!(buf, "CREATE TABLE IF NOT EXISTS {} (", table_name).unwrap();
    if let Some(kind) = &ctx.id_type {
        write_column("id", kind, &table_name, &mut buf)?;
    }
    for (name, kind) in &ctx.fields {
        write_column(name, kind, &table_name, &mut buf)?;
    }
    buf.push_str("\n)");
    Ok(buf)
}
