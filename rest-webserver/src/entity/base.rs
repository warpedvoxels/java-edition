use uuid::Uuid;

use crate::app::WebserverState;

pub trait Entity<T> where T: Entity<T> {
    fn up(state: &WebserverState) -> T;

    fn find(state: &WebserverState, id: Uuid) -> Option<T>;

    fn find_all(state: &WebserverState) -> Vec<T>;

    fn create(&self, state: &WebserverState) -> T;

    fn update(&self, state: &WebserverState) -> T;
}