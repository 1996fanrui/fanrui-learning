use lazy_static::lazy_static;

struct TimeZoneDatabase;

impl TimeZoneDatabase {
    fn new() -> Self {
        println!("Initializing TimeZoneDatabase");
        TimeZoneDatabase
    }

    fn get_instance() -> &'static TimeZoneDatabase {
        lazy_static! {
            static ref DB: TimeZoneDatabase = TimeZoneDatabase::new();
        }
        &DB
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_same_instance() {
        let db1 = TimeZoneDatabase::get_instance();
        let db2 = TimeZoneDatabase::get_instance();
        // Should be the same instance
        assert_eq!(db1 as *const _, db2 as *const _);
    }
}
