package com.dream.base.regular;

import java.util.regex.Pattern;

/**
 * @author fanrui
 * @time 2020-05-07 11:37:10
 * 正则匹配关于创建 Pattern 对象的优化，只构建一个 Pattern 重复利用来提高效率
 */
public class PatternOptimize {

    private static final String REGULAR_MATCH_STR = ".*hello.*";
    private static final Pattern REGULAR_PATTERN = Pattern.compile(REGULAR_MATCH_STR);

    /**
     * 该方式，每次都会构建一个 str 的 Pattern 对象，所以效率较低
     *
     * @param str
     * @return
     */
    static boolean match(String str) {
        return str.matches(REGULAR_MATCH_STR);
    }

    /**
     * 用 REGULAR_MATCH_STR 构建一个 Pattern，
     * 然后重复利用这个 Pattern 进行匹配
     *
     * @param str
     * @return
     */
    static boolean matchOptimize(String str) {
        return REGULAR_PATTERN.matcher(str).matches();
    }

}
