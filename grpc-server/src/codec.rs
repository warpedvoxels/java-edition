use std::marker::PhantomData;

use bytes::{Buf, BufMut};
use tonic::{
    codec::{Codec, DecodeBuf, Decoder, EncodeBuf, Encoder},
    Status,
};

/// A [`Codec`] that implements `application/grpc+cbor` via the serde library.
#[derive(Debug, Clone)]
pub struct CborCodec<T, U>(PhantomData<(T, U)>);

#[derive(Debug)]
pub struct CborEncoder<T>(PhantomData<T>);

#[derive(Debug)]
pub struct CborDecoder<T>(PhantomData<T>);

impl<T: serde::Serialize> Encoder for CborEncoder<T> {
    type Item = T;
    type Error = Status;

    fn encode(&mut self, item: Self::Item, buf: &mut EncodeBuf<'_>) -> Result<(), Self::Error> {
        cbor4ii::serde::to_writer(&mut buf.writer(), &item)
            .map_err(|error| Status::internal(error.to_string()))
    }
}

impl<T: serde::de::DeserializeOwned> Decoder for CborDecoder<T> {
    type Item = T;
    type Error = Status;

    fn decode(&mut self, buf: &mut DecodeBuf<'_>) -> Result<Option<Self::Item>, Self::Error> {
        if !buf.has_remaining() {
            return Ok(None);
        }

        let item: Self::Item = cbor4ii::serde::from_reader(&mut buf.reader())
            .map_err(|e| Status::internal(e.to_string()))?;
        Ok(Some(item))
    }
}

impl<T, U> Codec for CborCodec<T, U>
where
    T: serde::Serialize + Send + 'static,
    U: serde::de::DeserializeOwned + Send + 'static,
{
    type Encode = T;
    type Decode = U;
    type Encoder = CborEncoder<T>;
    type Decoder = CborDecoder<U>;

    fn encoder(&mut self) -> Self::Encoder {
        CborEncoder(PhantomData)
    }

    fn decoder(&mut self) -> Self::Decoder {
        CborDecoder(PhantomData)
    }
}

impl<T, U> Default for CborCodec<T, U>
where
    T: serde::Serialize + Send + 'static,
    U: serde::de::DeserializeOwned + Send + 'static,
{
    fn default() -> Self {
        CborCodec(PhantomData)
    }
}

