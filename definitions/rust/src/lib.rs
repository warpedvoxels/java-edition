use std::fs;

use proc_macro::TokenStream;
use quote::format_ident;
use specification::SpecificationRoot;

mod specification;

#[proc_macro]
pub fn generate_entity_definitions(item: TokenStream) -> TokenStream {
    let mut path = item.to_string();
    path.pop();
    path.remove(0);
    
    let code = path
        .split("|")
        .map(|path| {
            let home = home::home_dir().expect("Could not find home directory");

            let root = home
                .join(".hexalite")
                .join("dev")
                .canonicalize()
                .expect("Could not find ~/.hexalite/dev directory");

            let file = root.join(path);
            let file = fs::read_to_string(&file).unwrap();

            let specification: SpecificationRoot =
                serde_yaml::from_str(file.as_str()).expect("Could not parse specification");

            let quotes = specification
                .entities
                .iter()
                .map(|specification| {
                    let struct_fields_name = specification
                        .fields
                        .iter()
                        .map(|(name, _)| format_ident!("{}", name.clone().trim().to_string()))
                        .collect::<Vec<_>>();
                    let struct_fields_kind = specification
                        .fields
                        .iter()
                        .map(|(_, field)| format_ident!("{}", field.kind.rust.trim().to_string()))
                        .collect::<Vec<_>>();
                    let name = format_ident!("{}", specification.name.trim());

                    quote::quote! {
                        #[derive(Debug, Clone)]
                        pub struct #name {
                            #(pub #struct_fields_name: #struct_fields_kind),*
                        }
                    }
                })
                .collect::<Vec<_>>();

            quote::quote! {
                #(#quotes)
                *
            }
        })
        .collect::<Vec<_>>();

    TokenStream::from(quote::quote! {
        #(#code)
        *
    })
}
