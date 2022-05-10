use std::any::Any;

use proc_macro::TokenStream;
use syn::{parse_macro_input, DataStruct, Fields, Type, Ident, LitStr};

#[proc_macro_derive(ExportFields)]
pub fn derive_export_fields(input: TokenStream) -> TokenStream {
    let input = parse_macro_input!(input as syn::DeriveInput);

    let fields = match &input.data {
        syn::Data::Struct(DataStruct {
            fields: Fields::Named(fields),
            ..
        }) => &fields.named,
        _ => panic!("#[derive(ExportFields)] can only be used on structs"),
    };
    let struct_name = &input.ident;
    let field_name = fields.iter().map(|f| {
        let ident = f.ident.as_ref().unwrap();
        LitStr::new(&ident.to_string(), ident.span())
    });
    let field_type  = fields.iter().map(|f| &f.ty);

    quote::quote! {
        impl #struct_name {
            pub fn fields() -> &'static phf::Map<&'static str, &'static str> {
                static FIELDS: phf::Map<&'static str, &'static str> = phf::phf_map! {
                    #(
                        #field_name => stringify!(#field_type)
                    ),*
                };
                &FIELDS
            }
        }
    }
    .into()
}
