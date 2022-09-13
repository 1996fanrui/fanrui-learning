package com.dream.base;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Optional demo.
 */
public class OptionalDemo {

    public static void main(String[] args) {
        // test for null, default value and throw exception
        try {
            String xxx = "xxx";
            String s = Optional.ofNullable(xxx).orElseThrow((Supplier<Throwable>) () -> new RuntimeException("xxxx1"));
            System.out.println(s);

            xxx = null;
            String value  = Optional.ofNullable(xxx).orElse("xxx2");
            System.out.println(value);

            Optional.ofNullable(null).orElseThrow((Supplier<Throwable>) () -> new RuntimeException("xxxx3"));
        } catch (Throwable throwable) {
            System.out.println(throwable);
        }

        // test for null and map
        try {
            Integer integer = 6;
            Long s = Optional.ofNullable(integer).map(Integer::longValue)
                    .orElseThrow((Supplier<Throwable>) () -> new RuntimeException("xxxx1"));
            System.out.println(s);

            integer = null;
            Optional.ofNullable(integer).map(Integer::longValue)
                    .orElseThrow((Supplier<Throwable>) () -> new RuntimeException("yyyy2"));
        } catch (Throwable throwable) {
            System.out.println(throwable);
        }

    }

}
