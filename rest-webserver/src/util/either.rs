use actix_web::Either;
use std::fmt::Debug;

pub fn try_either<L, R, LF, LE>(attempt: LF, default: R) -> Either<L, R>
where
    LF: FnOnce() -> Result<L, LE>,
{
    match attempt() {
        Ok(value) => Either::Left(value),
        Err(_) => Either::Right(default),
    }
}

pub fn try_either_both<L, R, LF, RF, LE, RE: Debug>(attempt: LF, attempt2: RF) -> Either<L, R>
where
    LF: FnOnce() -> Result<L, LE>,
    RF: FnOnce() -> Result<R, RE>,
{
    match attempt() {
        Ok(value) => Either::Left(value),
        Err(_) => Either::Right(attempt2().unwrap()),
    }
}
