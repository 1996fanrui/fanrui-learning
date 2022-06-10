package com.dream.jmh.demo;

import com.dream.jmh.BenchmarkBase;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class CPUBenchmark extends BenchmarkBase {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + CPUBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public String computeMd5() {
        UUID uuid = UUID.randomUUID();
        return md5(uuid.toString());
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有匹配的加密算法");
        }
    }

}
