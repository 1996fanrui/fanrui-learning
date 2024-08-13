use criterion::{criterion_group, criterion_main, Criterion};
use jiff::tz::TimeZone;
use jiff::{tz, Timestamp, Zoned};
use std::time::SystemTime;

fn jiff_zoned_time_zone_benchmark(c: &mut Criterion) {
    c.bench_function("Get default TimeZone::system()", |b| {
        b.iter(|| {
            TimeZone::system();
        })
    });
    //
    // c.bench_function("Get UTC-8 TimeZone", |b| {
    //     b.iter(|| {
    //         TimeZone::fixed(tz::offset(-8));
    //     })
    // });
    //
    // c.bench_function("Get UTC-8 TimeZone twice", |b| {
    //     b.iter(|| {
    //         TimeZone::fixed(tz::offset(-8));
    //         TimeZone::fixed(tz::offset(-8));
    //     })
    // });
    //
    // c.bench_function("Get UTC-8 TimeZone and clone", |b| {
    //     b.iter(|| {
    //         let _ = TimeZone::fixed(tz::offset(-8)).clone();
    //     })
    // });
    //
    // c.bench_function("Get America/Chicago TimeZone", |b| {
    //     b.iter(|| {
    //         let _ = TimeZone::get("America/Chicago");
    //     })
    // });
    //
    // c.bench_function("Get America/Chicago TimeZone twice", |b| {
    //     b.iter(|| {
    //         let _ = TimeZone::get("America/Chicago");
    //         let _ = TimeZone::get("America/Chicago");
    //     })
    // });
    //
    // c.bench_function("Get America/Chicago TimeZone and clone", |b| {
    //     b.iter(|| {
    //         let _ = TimeZone::get("America/Chicago").clone();
    //     })
    // });
    //
    // c.bench_function("Zoned::now().", |b| {
    //     b.iter(|| {
    //         Zoned::now();
    //     })
    // });

    // c.bench_function("Zoned::now() with time zone America/Chicago.", |b| {
    //     b.iter(|| {
    //         Zoned::now().with_time_zone(TimeZone::get("America/Chicago").unwrap());
    //     })
    // });
    //
    // c.bench_function("Zoned::new with time zone America/Chicago.", |b| {
    //     b.iter(|| {
    //         Zoned::new(Timestamp::now(), TimeZone::get("America/Chicago").unwrap());
    //     })
    // });

    // c.bench_function("Zoned::now step-by-step with time zone America/Chicago.", |b| {
    //     let time_zone = TimeZone::get("America/Chicago").unwrap();
    //     let zoned = Zoned::now();
    //     b.iter(|| {
    //         let timestamp = zoned.timestamp();
    //         let (offset, _, _) = time_zone.to_offset(timestamp);
    //         let datetime = offset.to_datetime(timestamp);
    //     })
    // });

    // c.bench_function("Zoned::now() with time zone UTC-8.", |b| {
    //     b.iter(|| {
    //         Zoned::now().with_time_zone(TimeZone::fixed(tz::offset(-8)));
    //     })
    // });
    //
    // c.bench_function("Zoned::new with time zone UTC-8.", |b| {
    //     b.iter(|| {
    //         Zoned::new(Timestamp::now(), TimeZone::fixed(tz::offset(-8)));
    //     })
    // });
}

criterion_group!(benches, jiff_zoned_time_zone_benchmark);
criterion_main!(benches);
