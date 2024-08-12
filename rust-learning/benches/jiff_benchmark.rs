use std::time::SystemTime;
use criterion::{criterion_group, criterion_main, Criterion};
use jiff::tz::TimeZone;
use jiff::{Timestamp, tz, Zoned};

fn jiff_zoned_time_zone_benchmark(c: &mut Criterion) {

    c.bench_function("Zoned::now()", |b| b.iter(|| {
        Zoned::now();
    }));

    c.bench_function("Zoned::now() with time zone.", |b| b.iter(|| {
        let zone = Some(TimeZone::fixed(tz::offset(-8)));
        match zone.clone() {
            Some(tz) => Zoned::now().with_time_zone(tz),
            None => Zoned::now(),
        };
    }));


    c.bench_function("Zoned::new with time zone.", |b| b.iter(|| {
        let zone = Some(TimeZone::fixed(tz::offset(-8)));
        match zone.clone() {
            Some(tz) => {
                let timestamp = Timestamp::try_from(SystemTime::now()).unwrap();
                Zoned::new(timestamp, tz)
            },
            None => Zoned::now(),
        };
    }));
}

criterion_group!(benches, jiff_zoned_time_zone_benchmark);
criterion_main!(benches);
