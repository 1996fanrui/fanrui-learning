package com.dream.design.pattern.action.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanrui03
 * @time 2020-06-28 01:02:20
 * 策略模式 Demo，适用于 非共享策略类的情况，即：每次要创建新的策略类
 * 创建策略类时，省略了大量的 if else 分支判断，优化为通过 Map 查表创建对象
 * 模仿 Flink 源码中 KeyedStateBackend 创建各种 State 的方式
 */
public class StrategyPatternNoShareDemo {

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

    // 策略工厂接口
    private interface StrategyFactory {
        Strategy create();
    }

    // Map 的 key 为策略类型，value 为 策略的工厂类
    private static final Map<String, StrategyFactory> STRATEGY_FACTORIES = new HashMap<>();

    /*
     * ConcreteStrategyA::new 是 Java 8 的新特性：方法引用
     * 等价于：
     *      new StrategyFactory() {
     *          @Override
     *          public Strategy create() {
     *              return new ConcreteStrategyA();
     *          }
     *      }
     * 等价于：  () -> new ConcreteStrategyA()
     */
    static {
        STRATEGY_FACTORIES.put("A", ConcreteStrategyA::new);
        STRATEGY_FACTORIES.put("B", ConcreteStrategyB::new);
    }

    public static Strategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }
        // 根据 type 获取对应的策略工厂，根据工厂创建出策略类
        StrategyFactory strategyFactory = STRATEGY_FACTORIES.get(type);
        return strategyFactory.create();
    }

    private final static String DEFAULT_STRATEGY_TYPE = "A";

    public static void main(String[] args) {
        String type = DEFAULT_STRATEGY_TYPE;
        if (args.length > 0) {
            type = args[0];
        }
        Strategy strategy = getStrategy(type);
        strategy.algorithmInterface();
    }

}
