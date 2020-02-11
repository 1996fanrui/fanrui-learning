package com.dream.hash.algo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author fanrui
 * 设计一种结构，在该结构中有如下三个功能，三个操作的时间复杂度要求都是 O（1）:
 * insert(key):将某个key加入到该结构，做到不重复加入。
 * delete(key):将原本在结构中的某个key移除。
 * getRandom(): 等概率随机返回结构中的任何一个key。
 */
public class RandomPool<K> {

    private HashMap<K, Integer> keyIndexMap;
    private ArrayList<K> indexKey;

    public RandomPool() {
        this.keyIndexMap = new HashMap<>();
        this.indexKey = new ArrayList<>();
    }

    public void insert(K key) {
        // RandomPool 中已经包含该 key，不需要添加，直接返回
        if (keyIndexMap.containsKey(key)) {
            return;
        }
        // Map 中增加 key ，value 为数组中对应的下标
        keyIndexMap.put(key, indexKey.size());
        // 数组中增加 key ，数组容量 +1
        indexKey.add(key);
    }

    public void delete(K key) {
        // RandomPool 中不包含该 key，不需要删除，直接返回
        if (!keyIndexMap.containsKey(key)) {
            return;
        }
        // 从 Map 中删除该 key，并拿到 key 对应的 index
        int deleteIndex = keyIndexMap.remove(key);

        // 数组容量 -1 为数组最后一个元素的 index，并拿到该元素
        int lastIndex = indexKey.size() - 1;
        K lastKey = indexKey.remove(lastIndex);

        // 更新数组，将原来数组最后一个元素放到删掉的坑位，并把最后的元素移除
        indexKey.set(deleteIndex, lastKey);

        // 更新 Map，拿到数组最后一个元素，并重新 put 到 Map 中，
        // 因为它在 Map 中的 Index 要更新了
        keyIndexMap.put(lastKey, deleteIndex);

    }

    public K getRandom() {
        if (indexKey.size() == 0) {
            return null;
        }
        // 0 ~ size -1
        int randomIndex = (int) (Math.random() * indexKey.size());
        return indexKey.get(randomIndex);
    }


    public static void main(String[] args) {
        RandomPool<String> pool = new RandomPool<String>();
        pool.insert("aaa");
        pool.insert("bbb");
        pool.insert("ccc");
        pool.insert("ddd");
        pool.insert("eee");
        pool.insert("fff");
        pool.insert("ggg");
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        pool.delete("aaa");
        System.out.println("--------------------");
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());

    }


}
