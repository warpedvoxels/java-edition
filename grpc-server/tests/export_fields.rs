#![feature(const_type_name)]

#[allow(dead_code)]
#[derive(hexalite_common::ExportFields)]
pub struct Hello {
    hello: Option<String>,
    world: Result<(), Option<Vec<String>>>,
}

#[test]
pub fn test_field_exporting_output() {
    let fields = Hello::fields();
    println!("{:?}", fields);

    assert_eq!(
        fields["hello"],
        "core::option::Option<alloc::string::String>"
    );
    assert_eq!(
        fields["world"],
        "core::result::Result<(), core::option::Option<alloc::vec::Vec<alloc::string::String>>>"
    );
}
