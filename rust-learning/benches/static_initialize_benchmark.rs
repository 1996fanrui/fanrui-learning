use criterion::{criterion_group, criterion_main, Criterion};
use rust_learning::static_lazy::once_lock::Config;

fn static_initialize_benchmark(c: &mut Criterion) {
    c.bench_function("Get static database without DB initialization.", |b| {
        b.iter(|| {
            // Database::new is only called once, so the sleep isn't effective.
            Config::new("database_url1".to_string(), 1000).get_database();
        })
    });
}

criterion_group!(benches, static_initialize_benchmark);
criterion_main!(benches);
