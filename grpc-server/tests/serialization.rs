use grpc_server::datatype::Username;

#[test]
pub fn test_username() {
    let username = Username::from("my_username");
    assert_eq!(username.value().unwrap(), "my_username");
    assert!(Username::from("my_username_is_too_long_aaaa")
        .value()
        .is_err());

    let serialized = serde_json::to_string(&username).unwrap();
    assert_eq!(serialized, r#""my_username""#);
}
