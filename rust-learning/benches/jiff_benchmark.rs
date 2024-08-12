use std::time::SystemTime;
use criterion::{criterion_group, criterion_main, Criterion};
use jiff::tz::TimeZone;
use jiff::{Timestamp, tz, Zoned};

fn jiff_zoned_time_zone_benchmark(c: &mut Criterion) {

    c.bench_function("Zoned::now() with time zone.", |b| b.iter(|| {
        Zoned::now().with_time_zone(TimeZone::fixed(tz::offset(-8)));
    }));


    c.bench_function("Zoned::new with time zone.", |b| b.iter(|| {
        Zoned::new(Timestamp::now(), TimeZone::fixed(tz::offset(-8)));
    }));
}

criterion_group!(benches, jiff_zoned_time_zone_benchmark);
criterion_main!(benches);
