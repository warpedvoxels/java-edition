use std::future::{ready, Ready};

use actix_identity::{CookieIdentityPolicy, IdentityService, RequestIdentity};
use actix_web::{
    body::EitherBody,
    cookie::time::Duration,
    dev::{forward_ready, Service, ServiceRequest, ServiceResponse, Transform},
    Error, HttpResponse,
};
use futures_util::future::LocalBoxFuture;

use crate::{
    app::{WebServerState, WebServerStateData},
    definitions::rest::Authorization,
};

pub struct Authentication {
    pub needs_internal: bool,
}

impl<S, B> Transform<S, ServiceRequest> for Authentication
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error>,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<EitherBody<B>>;
    type Error = Error;
    type InitError = ();
    type Transform = AuthenticationMiddleware<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ready(Ok(AuthenticationMiddleware {
            service,
            needs_internal: self.needs_internal,
        }))
    }
}

pub struct AuthenticationMiddleware<S> {
    service: S,
    needs_internal: bool,
}

impl<S, B> Service<ServiceRequest> for AuthenticationMiddleware<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error>,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<EitherBody<B>>;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    forward_ready!(service);

    fn call(&self, request: ServiceRequest) -> Self::Future {
        let state = request.app_data::<WebServerState>().unwrap();
        let identity = RequestIdentity::get_identity(&request).unwrap_or_default();
        let authorization = Authorization::decode(&identity, state);

        if authorization.is_err()
            || (self.needs_internal && !authorization.unwrap().claims.is_internal)
        {
            let (request, _) = request.into_parts();
            let response = HttpResponse::Unauthorized().finish().map_into_right_body();
            return Box::pin(async { Ok(ServiceResponse::new(request, response)) });
        }

        let response = self.service.call(request);
        Box::pin(async move { response.await.map(ServiceResponse::map_into_left_body) })
    }
}

pub fn create_identity_service(
    state: &WebServerStateData,
) -> IdentityService<CookieIdentityPolicy> {
    if state.settings.grpc.services.identity.secret_key.len() < 32 {
        panic!("The secret key for the identity service must have at least 32 characters.");
    }
    IdentityService::new(
        CookieIdentityPolicy::new(state.settings.grpc.services.identity.secret_key.as_ref())
            .name(state.settings.grpc.services.identity.cookie_name.clone())
            .secure(state.settings.grpc.services.identity.is_secure)
            .max_age(Duration::seconds(
                state
                    .settings
                    .grpc
                    .services
                    .identity
                    .expiration
                    .num_seconds(),
            )),
    )
}
