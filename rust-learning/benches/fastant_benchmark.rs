use criterion::{criterion_group, criterion_main, Criterion};
use std::time::Instant;

fn fastant_benchmark(c: &mut Criterion) {
    c.bench_function("fastant::Instant::now()", |b| {
        b.iter(|| {
            let _ = fastant::Instant::now();
        })
    });

    c.bench_function("Instant::now()", |b| {
        b.iter(|| {
            let _ = Instant::now();
        })
    });

    c.bench_function("fastant::Instant::elapsed", |b| {
        b.iter(|| {
            let start = fastant::Instant::now();
            let _: std::time::Duration = start.elapsed();
        })
    });

    c.bench_function("Instant::now()::elapsed", |b| {
        b.iter(|| {
            let start = Instant::now();
            let _: std::time::Duration = start.elapsed();
        })
    });
}

criterion_group!(benches, fastant_benchmark);
criterion_main!(benches);
