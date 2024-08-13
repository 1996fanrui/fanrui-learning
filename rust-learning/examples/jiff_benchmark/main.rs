use jiff::tz::TimeZone;

fn main() {
    // let _ = Zoned::now();
    // let _ = TimeZone::get("America/Chicago");
    // let _ = TimeZone::get("America/Chicago");
    //
    // let _ = TimeZone::fixed(tz::offset(-8));
    // let _ = TimeZone::fixed(tz::offset(-8));

    // Zoned::new(Timestamp::now(), TimeZone::fixed(tz::offset(-8)));

    // Zoned::new(Timestamp::now(), TimeZone::get("America/Chicago").unwrap());
    //
    // Zoned::now().with_time_zone(TimeZone::get("America/Chicago").unwrap());

    TimeZone::system();
    TimeZone::system();

    // let zoned = Zoned::now();
    // let time_zone = TimeZone::get("America/Chicago").unwrap();
    //
    for i in 1..=200_000_000 {
        TimeZone::system();
        TimeZone::system();
    }

    // let inner = ZonedInner { timestamp, datetime, offset, time_zone };


}
