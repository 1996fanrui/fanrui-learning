package com.dream.flink.kryo.map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Maps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author fanrui03
 * @time 2020-08-30 15:46:17
 * 自定义的特殊类注册 Kryo 序列化
 */
public class LinkedHashMapKryoTest {

  public static void main(String[] args) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {

    HashMap<String, LinkedHashMap<String, Boolean>> map = Maps.newHashMap();

    LinkedHashMap<String, Boolean> linkedHashMap1 = new LinkedHashMapEnhance<>(500);
    linkedHashMap1.put("t1", true);
    linkedHashMap1.put("t2", false);
    map.put("t", linkedHashMap1);

    LinkedHashMap<String, Boolean> linkedHashMap2 = new LinkedHashMapEnhance<>(500);
    linkedHashMap2.put("u1", true);
    linkedHashMap2.put("u2", false);
    map.put("u", linkedHashMap2);

    System.out.println(String.format("origin : %s", map));

    Kryo kryo = new Kryo();

    String className = "com.dream.flink.kryo.LinkedHashSet";
    String serializerClassName = "com.dream.flink.kryo.map.LinkedHashSetSerializer";

//    kryo.register(LinkedHashSet.class, new LinkedHashSetSerializer());
    kryo.register(Class.forName(className),
        (Serializer) Class.forName(serializerClassName).newInstance());

    // flink 注册 Kryo 序列化器的代码
//    env.getConfig().registerTypeWithKryoSerializer(LinkedHashSet.class, LinkedHashSetSerializer.class);
//    env.getConfig().registerTypeWithKryoSerializer(Class.forName(className),
//                                                  Class.forName(serializerClassName));

    try (Output output = new Output(new FileOutputStream("/Users/fanrui03/Downloads/kryo.ser"))) {
      kryo.writeClassAndObject(output, map);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    try (Input input = new Input(new FileInputStream("/Users/fanrui03/Downloads/kryo.ser"))) {
      HashMap<String, LinkedHashMap<String, Boolean>> result =
          (HashMap<String, LinkedHashMap<String, Boolean>>) kryo.readClassAndObject(input);
      System.out.println(String.format("result : %s", result));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
