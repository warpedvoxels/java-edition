use lazy_static::lazy_static;
use std::future::Future;
use tokio::runtime::Runtime;

lazy_static! {
    static ref RUNTIME: Runtime = tokio::runtime::Builder::new_multi_thread()
        .enable_all()
        .build()
        .unwrap();
}

pub fn exec<F: Future>(future: F) -> F::Output {
    RUNTIME.block_on(future)
}
