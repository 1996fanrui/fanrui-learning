package com.dream.flink.runtime;

/**
 * The pseudo-code of Flink working mode.
 *
 * One task has 2 operators: RedisLookupJoin and Map.
 *
 * RedisLookupJoin: 7 ms
 *   - Query redis(IO): 6 ms
 *   - Other operations(CPU) : 1 ms
 * Map(CPU) : 3 ms
 */
public class TaskWorkingModel {

    public OutputType mockFlinkTaskProcessingData(InputType input) {
        Type joinResult = redisLookupJoin(input);   // cost 7 ms
        OutputType mapResult = mapFunction(joinResult);   // cost 3ms

        return mapResult;
    }

    private Type redisLookupJoin(InputType input) {
        Type redisResult = queryRedis(input); // IO  cost 6 ms
        Type joinResult = join(redisResult);  // CPU cost 1 ms

        return joinResult;
    }

    private Type queryRedis(InputType input) {
        return new Type();
    }

    private Type join(Type input) {
        return input;
    }

    private OutputType mapFunction(Type input) {
        return new OutputType();
    }

    private static class InputType {

    }

    private static class OutputType {

    }

    private static class Type {

    }
}
