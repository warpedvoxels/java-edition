use std::ffi;
use libc::{c_char, size_t};

#[repr(C)]
#[derive(Debug)]
pub struct ByteBuf {
    pub len: size_t,
    pub data: *const u8,
}

pub unsafe fn http_uri(uri: *const c_char, prefer_ssl: bool) -> String {
    if prefer_ssl {
        format!("https://{}", as_str(uri))
    } else {
        format!("http://{}", as_str(uri))
    }
}

impl<T> From<&T> for ByteBuf
    where
        T: serde::ser::Serialize,
{
    fn from(value: &T) -> Self {
        let buffer = cbor4ii::serde::to_vec(Vec::new(), value).unwrap();
        let len = buffer.len() as size_t;
        let bytes = buffer.into_boxed_slice();
        let data = Box::into_raw(bytes) as *const u8;
        Self { len, data }
    }
}

#[no_mangle]
pub unsafe extern "C" fn free_buf(buf: *const ByteBuf) {
    if !buf.is_null() {
        let ptr = buf as *mut ByteBuf;
        let buf = Box::from_raw(ptr);
        buf.vec();
    }
}

#[no_mangle]
pub unsafe extern "C" fn get_buf_data(buf: *const ByteBuf) -> *const u8 {
    (*buf).data
}

#[no_mangle]
pub unsafe extern "C" fn get_buf_len(buf: *const ByteBuf) -> size_t {
    (*buf).len
}

impl ByteBuf {
    pub unsafe fn vec(&self) -> Vec<u8> {
        Vec::from_raw_parts(self.data as *mut u8, self.len as usize, self.len as usize)
    }
}

pub unsafe fn as_str<'a>(pointer: *const c_char) -> &'a str {
    ffi::CStr::from_ptr(pointer).to_str().unwrap()
}
