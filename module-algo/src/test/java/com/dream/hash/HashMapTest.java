package com.dream.hash;

import org.junit.Test;

import java.util.*;

public class HashMapTest {


    private class Student{

        int id;
        String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return id == student.id &&
                    Objects.equals(name, student.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Test
    public void testHashSet(){
        Set<Student> hashSet = new HashSet<>();

        Student a = new Student(1,"aaa");
        Student b = new Student(1,"aaa");
        Student c = new Student(1,"aaa");

        hashSet.add(a);
        hashSet.add(b);
        hashSet.add(c);

        // 3
        System.out.println(hashSet.size());
    }

    @Test
    public void testHashMapSort(){
        HashMap map = new HashMap();
        map.size();

        System.out.println(Integer.valueOf(1).hashCode());


        map.put(1,2);
        map.put(2,2);
        map.put(3,2);
        map.put(4,2);
        map.put(5,2);
        map.put(6,2);
        map.put(7,2);
        map.put(8,2);
        map.put(9,2);
        map.put(10,2);
        map.put(11,2);
        map.put(12,2);
        map.put(13,2);
        map.put(14,2);
        map.put(15,2);
        map.put(16,2);
        map.put(17,2);
        map.put(18,2);
    }

    @Test
    public void testLinkedHashMapSort(){
        Map map = new LinkedHashMap();

    }
}
