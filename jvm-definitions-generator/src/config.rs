//! https://github.com/tokio-rs/prost/blob/master/prost-build/src/lib.rs
use std::{
    collections::{hash_map, HashMap},
    fmt::{Display, Formatter},
    fs,
    io::{Error, ErrorKind},
    iter,
    path::{Path, PathBuf},
};

use anyhow::Result;
use heck::{ToLowerCamelCase, ToUpperCamelCase};
use itertools::Itertools;
use prost::Message;
use prost_types::{FileDescriptorProto, FileDescriptorSet};
use xshell::Shell;

use crate::code_generator::CodeGenerator;

#[derive(Debug)]
pub struct DefGeneratorConfig {
    output_dir: PathBuf,
    pub base_package: String,
    pub(crate) type_attrs: PathMap<String>,
    pub(crate) field_attrs: PathMap<String>,
    pub(crate) extern_paths: HashMap<String, String>,
}

impl Default for DefGeneratorConfig {
    fn default() -> Self {
        Self {
            type_attrs: PathMap::default(),
            field_attrs: PathMap::default(),
            extern_paths: HashMap::new(),
            base_package: String::from("org.hexalite.network.definition"),
            output_dir: hexalite_common::dirs::get_source_path()
                .unwrap()
                .join("kotlin-grpc-client/src/main/kotlin/org/hexalite/network/definition"),
        }
    }
}

impl DefGeneratorConfig {
    pub fn type_attr<P, A>(&mut self, path: P, attribute: A) -> &mut Self
    where
        P: AsRef<str>,
        A: AsRef<str>,
    {
        self.type_attrs
            .insert(path.as_ref().to_string(), attribute.as_ref().to_string());
        self
    }

    pub fn field_attr<P, A>(&mut self, path: P, attribute: A) -> &mut Self
    where
        P: AsRef<str>,
        A: AsRef<str>,
    {
        self.field_attrs
            .insert(path.as_ref().to_string(), attribute.as_ref().to_string());
        self
    }

    pub fn extern_path<P1, P2>(&mut self, proto_path: P1, rust_path: P2) -> &mut Self
    where
        P1: Into<String>,
        P2: Into<String>,
    {
        self.extern_paths
            .insert(proto_path.into(), rust_path.into());
        self
    }

    pub fn compile(
        &mut self,
        protos: &[impl AsRef<Path>],
        includes: &[impl AsRef<Path>],
    ) -> Result<()> {
        let sh = Shell::new().unwrap();
        let descriptor_path = tempfile::Builder::new().prefix("jvm-def-gen").tempdir()?;
        fs::create_dir_all(descriptor_path.path()).unwrap();
        let descriptor_path = descriptor_path.path().join("def-descriptor-set");
        let mut inc = Vec::<&str>::new();
        for include in includes {
            inc.push("-I");
            inc.push(include.as_ref().to_str().unwrap())
        }
        for proto in protos {
            inc.push(proto.as_ref().to_str().unwrap());
        }
        xshell::cmd!(
            sh,
            "protoc --include_imports --include_source_info -o {descriptor_path} {inc...}"
        )
        .run()?;
        let buf = fs::read(descriptor_path)?;
        let descriptor_set = FileDescriptorSet::decode(&*buf).map_err(|error| {
            Error::new(
                ErrorKind::InvalidInput,
                format!("invalid FileDescriptorSet: {}", error),
            )
        })?;

        let requests = descriptor_set
            .file
            .into_iter()
            .map(|descriptor| {
                (
                    Module::from_protobuf_package_name(descriptor.package()),
                    descriptor,
                )
            })
            .collect::<Vec<_>>();

        let file_names = requests
            .iter()
            .map(|req| (req.0.clone(), req.0.to_file_name_or("_")))
            .collect::<HashMap<Module, String>>();

        let modules = self.generate(requests)?;
        for (module, content) in &modules {
            let file_name = file_names
                .get(module)
                .expect("every module should have a filename");
            let output_path = self.output_dir.join(file_name);

            let previous_content = fs::read(&output_path);

            if previous_content
                .map(|previous_content| previous_content == content.as_bytes())
                .unwrap_or(false)
            {
                println!("unchanged: {file_name:?}");
            } else {
                println!("writing: {file_name:?}");
                fs::write(output_path, content)?;
            }
        }

        Ok(())
    }

    /// Processes a set of modules and file descriptors, returning a map of modules to generated
    /// code contents.
    ///
    /// This is generally used when control over the output should not be managed by Prost,
    /// such as in a flow for a `protoc` code generating plugin. When compiling as part of a
    /// `build.rs` file, instead use [`compile_protos()`].
    pub fn generate(
        &mut self,
        requests: Vec<(Module, FileDescriptorProto)>,
    ) -> Result<HashMap<Module, String>> {
        let mut modules = HashMap::new();
        let mut packages = HashMap::new();

        let extern_paths = ExternPaths::new(self.extern_paths.clone(), true, self)
            .map_err(|error| Error::new(ErrorKind::InvalidInput, error))?;

        for request in requests {
            // Only record packages that have services
            if !request.1.service.is_empty() {
                packages.insert(request.0.clone(), request.1.package().to_string());
            }

            let buf = modules.entry(request.0).or_insert_with(String::new);
            CodeGenerator::generate(self, &extern_paths, request.1, buf);
        }

        Ok(modules)
    }
}

fn validate_proto_path(path: &str) -> Result<(), String> {
    if path.chars().next().map(|c| c != '.').unwrap_or(true) {
        return Err(format!(
            "Protobuf paths must be fully qualified (begin with a leading '.'): {}",
            path
        ));
    }
    if path.split('.').skip(1).any(str::is_empty) {
        return Err(format!("invalid fully-qualified Protobuf path: {}", path));
    }
    Ok(())
}

#[derive(Debug)]
pub struct ExternPaths {
    extern_paths: HashMap<String, String>,
    base_package: String,
}

impl ExternPaths {
    pub fn new(
        paths: HashMap<String, String>,
        prost_types: bool,
        config: &DefGeneratorConfig,
    ) -> Result<ExternPaths, String> {
        let mut extern_paths = ExternPaths {
            extern_paths: paths,
            base_package: config.base_package.clone(),
        };

        if prost_types {
            extern_paths.insert(
                ".google.protobuf".to_string(),
                "kotlinx.datetime".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.Timestamp".to_string(),
                "kotlinx.datetime.Instant".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.BoolValue".to_string(),
                "Boolean".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.BytesValue".to_string(),
                "ByteArray".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.DoubleValue".to_string(),
                "Double".to_string(),
            )?;
            extern_paths.insert(".google.protobuf.Empty".to_string(), "Unit".to_string())?;
            extern_paths.insert(
                ".google.protobuf.FloatValue".to_string(),
                "Float".to_string(),
            )?;
            extern_paths.insert(".google.protobuf.Int32Value".to_string(), "Int".to_string())?;
            extern_paths.insert(
                ".google.protobuf.Int64Value".to_string(),
                "Long".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.StringValue".to_string(),
                "String".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.UInt32Value".to_string(),
                "Int".to_string(),
            )?;
            extern_paths.insert(
                ".google.protobuf.UInt64Value".to_string(),
                "Long".to_string(),
            )?;
        }

        Ok(extern_paths)
    }

    fn insert(&mut self, proto_path: String, rust_path: String) -> Result<(), String> {
        validate_proto_path(&proto_path)?;
        match self.extern_paths.entry(proto_path) {
            hash_map::Entry::Occupied(occupied) => {
                return Err(format!(
                    "duplicate extern Protobuf path: {}",
                    occupied.key()
                ));
            }
            hash_map::Entry::Vacant(vacant) => vacant.insert(rust_path),
        };
        Ok(())
    }

    pub fn resolve_ident(&self, pb_ident: &str) -> Option<String> {
        // protoc should always give fully qualified identifiers.
        assert_eq!(".", &pb_ident[..1]);

        if let Some(rust_path) = self.extern_paths.get(pb_ident) {
            return Some(rust_path.clone());
        }

        // TODO(danburkert): there must be a more efficient way to do this, maybe a trie?
        for (idx, _) in pb_ident.rmatch_indices('.') {
            if let Some(rust_path) = self.extern_paths.get(&pb_ident[..idx]) {
                let mut segments = pb_ident[idx + 1..].split('.');
                let ident_type = segments.next_back().map(to_upper_camel);

                return Some(
                    rust_path
                        .split("::")
                        .chain(segments)
                        .enumerate()
                        .map(|(idx, segment)| {
                            if idx == 0 && segment == "crate" {
                                // If the first segment of the path is 'crate', then do not escape
                                // it into a raw identifier, since it's being used as the keyword.
                                self.base_package.clone()
                            } else {
                                to_camel(segment)
                            }
                        })
                        .chain(ident_type.into_iter())
                        .join("."),
                );
            }
        }

        None
    }
}

/// Converts a `snake_case` identifier to an `UpperCamel` case Rust type identifier.
pub fn to_upper_camel(s: &str) -> String {
    let mut ident = s.to_upper_camel_case();

    // Suffix an underscore for the `Self` Rust keyword as it is not allowed as raw identifier.
    if ident == "Self" {
        ident += "_";
    }
    ident
}

pub fn to_camel(s: &str) -> String {
    let mut ident = s.to_lower_camel_case();

    // Use a raw identifier if the identifier matches a Rust keyword:
    // https://doc.rust-lang.org/reference/keywords.html.
    match ident.as_str() {
        // 2015 strict keywords.
        | "as" | "break" | "const" | "continue" | "else" | "enum" | "false"
        | "fn" | "for" | "if" | "impl" | "in" | "let" | "loop" | "match" | "mod" | "move" | "mut"
        | "pub" | "ref" | "return" | "static" | "struct" | "trait" | "true"
        | "type" | "unsafe" | "use" | "where" | "while"
        // 2018 strict keywords.
        | "dyn"
        // 2015 reserved keywords.
        | "abstract" | "become" | "box" | "do" | "final" | "macro" | "override" | "priv" | "typeof"
        | "unsized" | "virtual" | "yield"
        // 2018 reserved keywords.
        | "async" | "await" | "try" => ident.insert_str(0, "r#"),
        // the following keywords are not supported as raw identifiers and are therefore suffixed with an underscore.
        "self" | "super" | "extern" | "crate" => ident += "_",
        _ => (),
    }
    ident
}

/// A Rust module path for a Protobuf package.
#[derive(Clone, Debug, PartialEq, Eq, Hash, PartialOrd, Ord)]
pub struct Module {
    components: Vec<String>,
}

impl Module {
    /// Construct a module path from an iterator of parts.
    pub fn from_parts<I>(parts: I) -> Self
    where
        I: IntoIterator,
        I::Item: Into<String>,
    {
        Self {
            components: parts.into_iter().map(|s| s.into()).collect(),
        }
    }

    /// Construct a module path from a Protobuf package name.
    ///
    /// Constituent parts are automatically converted to snake case in order to follow
    /// Rust module naming conventions.
    pub fn from_protobuf_package_name(name: &str) -> Self {
        Self {
            components: name
                .split('.')
                .filter(|s| !s.is_empty())
                .map(to_camel)
                .collect(),
        }
    }

    /// An iterator over the parts of the path.
    pub fn parts(&self) -> impl Iterator<Item = &str> {
        self.components.iter().map(|s| s.as_str())
    }

    /// Format the module path into a filename for generated Rust code.
    ///
    /// If the module path is empty, `default` is used to provide the root of the filename.
    pub fn to_file_name_or(&self, default: &str) -> String {
        let mut root = if self.components.is_empty() {
            default.to_owned()
        } else {
            self.components.join(".")
        };

        root.push_str(".kt");

        root
    }

    /// The number of parts in the module's path.
    pub fn len(&self) -> usize {
        self.components.len()
    }

    /// Whether the module's path contains any components.
    pub fn is_empty(&self) -> bool {
        self.components.is_empty()
    }
}

impl Display for Module {
    fn fmt(&self, f: &mut Formatter) -> std::fmt::Result {
        let mut parts = self.parts();
        if let Some(first) = parts.next() {
            f.write_str(first)?;
        }
        for part in parts {
            f.write_str("::")?;
            f.write_str(part)?;
        }
        Ok(())
    }
}

#[derive(Debug, Default)]
pub(crate) struct PathMap<T>(pub(crate) HashMap<String, T>);

impl<T> PathMap<T> {
    /// Inserts a new matcher and associated value to the path map.
    pub(crate) fn insert(&mut self, matcher: String, value: T) {
        self.0.insert(matcher, value);
    }

    /// Returns a iterator over all the value matching the given fd_path and associated suffix/prefix path
    pub(crate) fn get(&self, fq_path: &str) -> Iter<'_, T> {
        Iter::new(self, fq_path.to_string())
    }

    /// Returns a iterator over all the value matching the path `fq_path.field` and associated suffix/prefix path
    pub(crate) fn get_field(&self, fq_path: &str, field: &str) -> Iter<'_, T> {
        Iter::new(self, format!("{}.{}", fq_path, field))
    }

    /// Returns the first value found matching the given path
    /// If nothing matches the path, suffix paths will be tried, then prefix paths, then the global path
    #[allow(unused)]
    pub(crate) fn get_first<'a>(&'a self, fq_path: &'_ str) -> Option<&'a T> {
        self.find_best_matching(fq_path)
    }

    /// Returns the first value found best matching the path
    /// See [sub_path_iter()] for paths test order
    fn find_best_matching(&self, full_path: &str) -> Option<&T> {
        sub_path_iter(full_path).find_map(|path| {
            self.0
                .iter()
                .find(|(p, _)| p.as_str() == path)
                .map(|(_, v)| v)
        })
    }
}

/// Iterator inside a PathMap that only returns values that matches a given path
pub(crate) struct Iter<'a, T> {
    iter: std::collections::hash_map::Iter<'a, String, T>,
    path: String,
}

impl<'a, T> Iter<'a, T> {
    fn new(map: &'a PathMap<T>, path: String) -> Self {
        Self {
            iter: map.0.iter(),
            path,
        }
    }

    fn is_match(&self, path: &str) -> bool {
        sub_path_iter(self.path.as_str()).any(|p| p == path)
    }
}

impl<'a, T> std::iter::Iterator for Iter<'a, T> {
    type Item = &'a T;

    fn next(&mut self) -> Option<Self::Item> {
        loop {
            match self.iter.next() {
                Some((p, v)) => {
                    if self.is_match(p) {
                        return Some(v);
                    }
                }
                None => return None,
            }
        }
    }
}

impl<'a, T> std::iter::FusedIterator for Iter<'a, T> {}

/// Given a fully-qualified path, returns a sequence of paths:
/// - the path itself
/// - the sequence of suffix paths
/// - the sequence of prefix paths
/// - the global path
///
/// Example: sub_path_iter(".a.b.c") -> [".a.b.c", "a.b.c", "b.c", "c", ".a.b", ".a", "."]
fn sub_path_iter(full_path: &str) -> impl Iterator<Item = &str> {
    // First, try matching the path.
    iter::once(full_path)
        // Then, try matching path suffixes.
        .chain(suffixes(full_path))
        // Then, try matching path prefixes.
        .chain(prefixes(full_path))
        // Then, match the global path.
        .chain(iter::once("."))
}

/// Given a fully-qualified path, returns a sequence of fully-qualified paths which match a prefix
/// of the input path, in decreasing path-length order.
///
/// Example: prefixes(".a.b.c.d") -> [".a.b.c", ".a.b", ".a"]
fn prefixes(fq_path: &str) -> impl Iterator<Item = &str> {
    std::iter::successors(Some(fq_path), |path| {
        #[allow(unknown_lints, clippy::manual_split_once)]
        path.rsplitn(2, '.').nth(1).filter(|path| !path.is_empty())
    })
    .skip(1)
}

/// Given a fully-qualified path, returns a sequence of paths which match the suffix of the input
/// path, in decreasing path-length order.
///
/// Example: suffixes(".a.b.c.d") -> ["a.b.c.d", "b.c.d", "c.d", "d"]
fn suffixes(fq_path: &str) -> impl Iterator<Item = &str> {
    std::iter::successors(Some(fq_path), |path| {
        #[allow(unknown_lints, clippy::manual_split_once)]
        path.splitn(2, '.').nth(1).filter(|path| !path.is_empty())
    })
    .skip(1)
}
