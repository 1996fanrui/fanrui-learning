use std::sync::OnceLock;
use std::thread::sleep;
use std::time::Duration;

pub struct Database {
    // Mock the database connection
}

impl Database {
    fn new(connection_string: &str, sleep_ms: u64) -> Self {
        println!(
            "Initializing Database with connection string: {}",
            connection_string
        );
        sleep(Duration::from_millis(sleep_ms));
        Database {}
    }
}

pub struct Config {
    connection_string: String,
    sleep_ms: u64,
}

impl Config {
    pub fn new(connection_string: String, sleep_ms: u64) -> Self {
        Config {
            connection_string,
            sleep_ms,
        }
    }

    pub fn get_database(&self) -> &'static Database {
        static DB: OnceLock<Database> = OnceLock::new();

        // note: static_lazy cannot call self related field.
        // Database::new is only called once.
        DB.get_or_init(|| Database::new(&self.connection_string, self.sleep_ms))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_same_instance() {
        let db1 = Config::new("database_url1".to_string(), 1000).get_database();
        let db2 = Config::new("database_url2".to_string(), 1000).get_database();

        // Should be the same instance, both of them use database_url1.
        assert_eq!(db1 as *const _, db2 as *const _);
    }
}
