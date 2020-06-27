package com.dream.design.pattern.action.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanrui03
 * @time 2020-06-28 01:02:20
 * 策略模式简易 Demo，适用于 共享策略类的情况
 */
public class StrategyPatternDemo {

    public interface Strategy {
        void algorithmInterface();
    }

    public static class ConcreteStrategyA implements Strategy {
        @Override
        public void algorithmInterface() {
            //具体的算法...
            System.out.println("Strategy A");
        }
    }

    public static class ConcreteStrategyB implements Strategy {
        @Override
        public void algorithmInterface() {
            //具体的算法...
            System.out.println("Strategy B");
        }
    }


    /**
     * 无状态的策略类，只是纯粹的算法实现，每种策略类只需要创建一个即可
     * 提前创建好所有策略类，缓存在 Map 中，每个根据 type 去 Map 中 get 即可
     */
    public static class StrategyFactory {
        private static final Map<String, Strategy> strategies = new HashMap<>();

        static {
            strategies.put("A", new ConcreteStrategyA());
            strategies.put("B", new ConcreteStrategyB());
        }

        public static Strategy getStrategy(String type) {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("type should not be empty.");
            }
            return strategies.get(type);
        }
    }

    private final static String DEFAULT_STRATEGY_TYPE = "A";

    public static void main(String[] args) {
        String type = DEFAULT_STRATEGY_TYPE;
        if (args.length > 0) {
            type = args[0];
        }
        Strategy strategy = StrategyFactory.getStrategy(type);
        strategy.algorithmInterface();
    }

}
