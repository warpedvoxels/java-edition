use std::collections::HashMap;
use std::fmt::Write;

use itertools::{Either, Itertools};
use multimap::MultiMap;
use prost_types::{
    field_descriptor_proto::{Label, Type},
    DescriptorProto, EnumDescriptorProto, EnumValueDescriptorProto, FieldDescriptorProto,
    FileDescriptorProto, OneofDescriptorProto,
};

use crate::config::{to_camel, to_upper_camel, DefGeneratorConfig, ExternPaths};

#[derive(PartialEq)]
enum Syntax {
    Proto2,
    Proto3,
}

pub struct CodeGenerator<'a> {
    config: &'a mut DefGeneratorConfig,
    package: String,
    syntax: Syntax,
    extern_paths: &'a ExternPaths,
    depth: u8,
    path: Vec<i32>,
    buf: &'a mut String,
}

fn push_indent(buf: &mut String, depth: u8) {
    for _ in 0..depth {
        buf.push_str("    ");
    }
}

fn strip_enum_prefix<'a>(prefix: &str, name: &'a str) -> &'a str {
    let stripped = name.strip_prefix(prefix).unwrap_or(name);

    // If the next character after the stripped prefix is not
    // uppercase, then it means that we didn't have a true prefix -
    // for example, "Foo" should not be stripped from "Foobar".
    if stripped
        .chars()
        .next()
        .map(char::is_uppercase)
        .unwrap_or(false)
    {
        stripped
    } else {
        name
    }
}

impl<'a> CodeGenerator<'a> {
    fn push_indent(&mut self) {
        push_indent(self.buf, self.depth);
    }

    pub fn generate(
        config: &mut DefGeneratorConfig,
        extern_paths: &ExternPaths,
        file: FileDescriptorProto,
        buf: &mut String,
    ) {
        let mut source_info = file
            .source_code_info
            .expect("no source code info in request");
        source_info.location.retain(|location| {
            let len = location.path.len();
            len > 0 && len % 2 == 0
        });
        source_info
            .location
            .sort_by_key(|location| location.path.clone());

        let syntax = match file.syntax.as_deref() {
            None | Some("proto2") => Syntax::Proto2,
            Some("proto3") => Syntax::Proto3,
            Some(s) => panic!("unknown syntax: {}", s),
        };

        let mut code_gen = CodeGenerator {
            config,
            package: file.package.unwrap_or_default(),
            syntax,
            extern_paths,
            depth: 0,
            path: Vec::new(),
            buf,
        };

        println!(
            "file: {:?}, package: {:?}",
            file.name.as_ref().unwrap(),
            code_gen.package
        );

        code_gen.path.push(4);
        if !code_gen.buf.starts_with("@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class)") {
            code_gen.buf.push_str("@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class)\n");
            writeln!(
                code_gen.buf,
                "package {}.{}",
                code_gen.config.base_package, code_gen.package
            ).unwrap();
        }
        for (idx, message) in file.message_type.into_iter().enumerate() {
            code_gen.path.push(idx as i32);
            code_gen.append_message(message);
            code_gen.path.pop();
        }
        code_gen.path.pop();

        code_gen.path.push(5);
        for (idx, desc) in file.enum_type.into_iter().enumerate() {
            code_gen.path.push(idx as i32);
            code_gen.append_enum(desc);
            code_gen.path.pop();
        }
        code_gen.path.pop();
    }

    fn append_message(&mut self, message: DescriptorProto) {
        println!("  message: {:?}", message.name());
        let message_name = message.name().to_string();
        let fq_message_name = format!(
            "{}{}.{}",
            if self.package.is_empty() { "" } else { "." },
            self.package,
            message.name()
        );

        // Skip external types.
        if self.extern_paths.resolve_ident(&fq_message_name).is_some() {
            return;
        }

        type NestedTypes = Vec<(DescriptorProto, usize)>;
        type MapTypes = HashMap<String, (FieldDescriptorProto, FieldDescriptorProto)>;
        let (_nested_types, map_types): (NestedTypes, MapTypes) = message
            .nested_type
            .into_iter()
            .enumerate()
            .partition_map(|(idx, nested_type)| {
                if nested_type
                    .options
                    .as_ref()
                    .and_then(|options| options.map_entry)
                    .unwrap_or(false)
                {
                    let key = nested_type.field[0].clone();
                    let value = nested_type.field[1].clone();
                    assert_eq!("key", key.name());
                    assert_eq!("value", value.name());

                    let name = format!("{}.{}", &fq_message_name, nested_type.name());
                    Either::Right((name, (key, value)))
                } else {
                    Either::Left((nested_type, idx))
                }
            });

        // Split the fields into a vector of the normal fields, and oneof fields.
        // Path indexes are preserved so that comments can be retrieved.
        type Fields = Vec<(FieldDescriptorProto, usize)>;
        type OneofFields = MultiMap<i32, (FieldDescriptorProto, usize)>;
        let (fields, oneof_fields): (Fields, OneofFields) = message
            .field
            .into_iter()
            .enumerate()
            .partition_map(|(idx, field)| {
                if field.proto3_optional.unwrap_or(false) {
                    Either::Left((field, idx))
                } else if let Some(oneof_index) = field.oneof_index {
                    Either::Right((oneof_index, (field, idx)))
                } else {
                    Either::Left((field, idx))
                }
            });

        self.append_type_attributes(&fq_message_name);
        self.push_indent();
        self.buf.push_str("@kotlinx.serialization.Serializable\n");
        writeln!(self.buf, "data class {} (", to_upper_camel(&message_name)).unwrap();
        self.depth += 1;
        self.path.push(2);
        for (field, idx) in fields {
            self.path.push(idx as i32);
            match field
                .type_name
                .as_ref()
                .and_then(|type_name| map_types.get(type_name))
            {
                Some(&(ref key, ref value)) => {
                    self.append_map_field(&fq_message_name, field, key, value)
                }
                None => self.append_field(&fq_message_name, field),
            }
            self.path.pop();
        }
        self.path.pop();

        self.path.push(8);
        for (idx, oneof) in message.oneof_decl.iter().enumerate() {
            let idx = idx as i32;

            let fields = match oneof_fields.get_vec(&idx) {
                Some(fields) => fields,
                None => continue,
            };

            self.path.push(idx);
            self.append_oneof_field(&message_name, &fq_message_name, oneof, fields);
            self.path.pop();
        }
        self.path.pop();

        self.depth -= 1;
        self.push_indent();
        self.buf.push_str(")\n");
    }

    fn append_type_attributes(&mut self, fq_message_name: &str) {
        assert_eq!(b'.', fq_message_name.as_bytes()[0]);
        for attribute in self.config.type_attrs.get(fq_message_name) {
            push_indent(self.buf, self.depth);
            self.buf.push_str(attribute);
            self.buf.push('\n');
        }
    }

    fn append_field_attributes(&mut self, fq_message_name: &str, field_name: &str) {
        assert_eq!(b'.', fq_message_name.as_bytes()[0]);
        for attribute in self
            .config
            .field_attrs
            .get_field(fq_message_name, field_name)
        {
            push_indent(self.buf, self.depth);
            self.buf.push_str(attribute);
            self.buf.push('\n');
        }
    }

    fn append_field(&mut self, fq_message_name: &str, field: FieldDescriptorProto) {
        let repeated = field.label == Some(Label::Repeated as i32);
        let optional = self.optional(&field);
        let ty = self.resolve_type(&field, fq_message_name);
        println!("    field: {:?}, type: {ty:?}", field.name(),);

        self.append_field_attributes(fq_message_name, field.name());
        self.push_indent();
        let _kind = if repeated {
            format!("List<{ty}>")
        } else if optional {
            format!("{ty}?")
        } else {
            ty.to_string()
        };
        writeln!(self.buf, "val {}: {ty},", to_camel(field.name())).unwrap();
    }

    fn append_map_field(
        &mut self,
        fq_message_name: &str,
        field: FieldDescriptorProto,
        key: &FieldDescriptorProto,
        value: &FieldDescriptorProto,
    ) {
        let key_ty = self.resolve_type(key, fq_message_name);
        let value_ty = self.resolve_type(value, fq_message_name);

        println!(
            "    map field: {:?}, key type: {key_ty:?}, value type: {value_ty:?}",
            field.name(),
        );

        self.append_field_attributes(fq_message_name, field.name());
        self.push_indent();
        writeln!(
            self.buf,
            "val {}: Map<{key_ty}, {value_ty}>,",
            to_camel(field.name())
        )
        .unwrap();
    }

    fn append_enum(&mut self, desc: EnumDescriptorProto) {
        println!("  enum: {:?}", desc.name());

        // Skip external types.
        let enum_name = &desc.name();
        let enum_values = &desc.value;

        let fq_enum_name = format!(
            "{}{}.{}",
            if self.package.is_empty() { "" } else { "." },
            self.package,
            enum_name
        );
        if self.extern_paths.resolve_ident(&fq_enum_name).is_some() {
            return;
        }

        self.append_type_attributes(&fq_enum_name);
        self.push_indent();
        self.buf.push_str("@kotlinx.serialization.Serializable\n");
        writeln!(self.buf, "enum class {} {{", to_upper_camel(enum_name)).unwrap();
        self.depth += 1;
        self.path.push(2);
        for (idx, value) in enum_values.iter().enumerate() {
            self.path.push(idx as i32);
            self.append_enum_value(&fq_enum_name, value, Some(to_upper_camel(enum_name)));
            self.path.pop();
        }
        self.path.pop();
        self.depth -= 1;
        self.push_indent();
        self.buf.push_str("}\n");
    }

    fn append_enum_value(
        &mut self,
        fq_enum_name: &str,
        value: &EnumValueDescriptorProto,
        prefix_to_strip: Option<String>,
    ) {
        self.append_field_attributes(fq_enum_name, value.name());
        self.push_indent();
        let name = to_upper_camel(value.name());
        let name_unprefixed = match prefix_to_strip {
            Some(prefix) => strip_enum_prefix(&prefix, &name),
            None => &name,
        };
        self.buf.push_str(name_unprefixed);
        self.buf.push_str(",\n");
    }
    fn append_oneof_field(
        &mut self,
        _message_name: &str,
        fq_message_name: &str,
        oneof: &OneofDescriptorProto,
        fields: &[(FieldDescriptorProto, usize)],
    ) {
        // todo: add support for oneofs
        // let ty = format!(
        //     "arrow.core.Either<{}>",
        //     fields
        //         .iter()
        //         .map(|(f, _)| { self.resolve_type(f, fq_message_name) })
        //         .join(", ")
        // );
        let ty = self.resolve_type(&fields.first().unwrap().0, fq_message_name);

        println!("    oneof: {:?}, ty: {ty}", oneof.name(),);

        self.append_field_attributes(fq_message_name, oneof.name());
        self.push_indent();
        writeln!(self.buf, "val {}: {ty},", to_camel(oneof.name()),).unwrap();
    }

    fn optional(&self, field: &FieldDescriptorProto) -> bool {
        if field.proto3_optional.unwrap_or(false) {
            return true;
        }

        if field.label() != Label::Optional {
            return false;
        }

        match field.r#type() {
            Type::Message => true,
            _ => self.syntax == Syntax::Proto2,
        }
    }

    fn resolve_type(&self, field: &FieldDescriptorProto, _fq_message_name: &str) -> String {
        match field.r#type() {
            Type::Float => String::from("Float"),
            Type::Double => String::from("Double"),
            Type::Uint32 | Type::Fixed32 => String::from("Int"),
            Type::Uint64 | Type::Fixed64 => String::from("Long"),
            Type::Int32 | Type::Sfixed32 | Type::Sint32 | Type::Enum => String::from("Int"),
            Type::Int64 | Type::Sfixed64 | Type::Sint64 => String::from("Long"),
            Type::Bool => String::from("Boolean"),
            Type::String => String::from("String"),
            Type::Bytes => String::from("ByteArray"),
            Type::Group | Type::Message => self.resolve_ident(field.type_name()),
        }
    }

    fn resolve_ident(&self, pb_ident: &str) -> String {
        // protoc should always give fully qualified identifiers.
        assert_eq!(".", &pb_ident[..1]);

        if let Some(proto_ident) = self.extern_paths.resolve_ident(pb_ident) {
            return proto_ident;
        }

        let mut local_path = self.package.split('.').peekable();

        // If no package is specified the start of the package name will be '.'
        // and split will return an empty string ("") which breaks resolution
        // The fix to this is to ignore the first item if it is empty.
        if local_path.peek().map_or(false, |s| s.is_empty()) {
            local_path.next();
        }

        let mut ident_path = pb_ident[1..].split('.');
        let ident_type = ident_path.next_back().unwrap();
        let mut ident_path = ident_path.peekable();

        // Skip path elements in common.
        while local_path.peek().is_some() && local_path.peek() == ident_path.peek() {
            local_path.next();
            ident_path.next();
        }

        local_path
            .map(|_| self.config.base_package.clone())
            .chain(ident_path.map(to_camel))
            .chain(std::iter::once(to_upper_camel(ident_type)))
            .join(".")
    }
}
