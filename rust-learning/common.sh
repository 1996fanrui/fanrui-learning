
cargo +nightly fmt
cargo bench --bench jiff-bench

sudo cargo flamegraph --release --example jiff_benchmark
