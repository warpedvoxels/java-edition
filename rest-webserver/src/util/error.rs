use actix_web::{error::InternalError, http::StatusCode, HttpResponse};

pub trait IntoHttpError<T> {
    fn http_error(
        self,
        message: &str,
        status_code: StatusCode,
    ) -> core::result::Result<T, actix_web::Error>;

    fn http_internal_error(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::INTERNAL_SERVER_ERROR)
    }

    fn http_bad_request(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::BAD_REQUEST)
    }

    fn http_not_found(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::NOT_FOUND)
    }

    fn http_unauthorized(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::UNAUTHORIZED)
    }

    fn http_forbidden(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::FORBIDDEN)
    }

    fn http_unprocessable_entity(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::UNPROCESSABLE_ENTITY)
    }

    fn http_conflict(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::CONFLICT)
    }

    fn http_not_implemented(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::NOT_IMPLEMENTED)
    }

    fn http_service_unavailable(self, message: &str) -> core::result::Result<T, actix_web::Error>
    where
        Self: std::marker::Sized,
    {
        self.http_error(message, StatusCode::SERVICE_UNAVAILABLE)
    }
}

impl<T, E: std::fmt::Debug> IntoHttpError<T> for core::result::Result<T, E> {
    fn http_error(
        self,
        message: &str,
        status_code: StatusCode,
    ) -> core::result::Result<T, actix_web::Error> {
        match self {
            Ok(val) => Ok(val),
            Err(err) => {
                if status_code == StatusCode::INTERNAL_SERVER_ERROR {
                    log::error!(
                        "An error occurred while processing a player creation request: {:?}",
                        err
                    );
                }
                Err(InternalError::new(message.to_string(), status_code).into())
            }
        }
    }
}

impl<T> IntoHttpError<T> for Option<T> {
    fn http_error(
        self,
        message: &str,
        status_code: StatusCode,
    ) -> core::result::Result<T, actix_web::Error> {
        match self {
            Some(val) => Ok(val),
            None => Err(InternalError::new(message.to_string(), status_code).into()),
        }
    }
}

pub trait ActixResult {
    fn result(self) -> Result<HttpResponse, actix_web::Error>;
}

impl ActixResult for HttpResponse {
    fn result(self) -> Result<HttpResponse, actix_web::Error> {
        if self.status().is_success() {
            return Ok(self);
        }
        Err(InternalError::new("An error ocurred.", self.status()).into())
    }
}
