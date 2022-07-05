use chrono::{FixedOffset, NaiveDateTime, DateTime};


pub type FixedDateTime = DateTime<FixedOffset>;

pub trait IntoFixed<T> {
    fn fixed(self) -> T;
}

impl IntoFixed<FixedDateTime> for NaiveDateTime {
    fn fixed(self) -> FixedDateTime {
        DateTime::from_utc(self, FixedOffset::east(0))
    }
}

impl IntoFixed<Option<FixedDateTime>> for Option<NaiveDateTime> {
    fn fixed(self) -> Option<FixedDateTime> {
        self.map(NaiveDateTime::fixed)
    }
}