package com.xx;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;

/**
 * @author yifanl
 * @Date 2020/5/28 11:18
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(null));
        HashSet<Long> longs = new HashSet<>();
        longs.add(100L);
        System.out.println(JSON.toJSONString(longs));
        System.out.println((788160087583L & 32L));
        System.out.println(longs.getClass().isArray());
    }


    static class A{
        private String aName;

        private B b;

        public A(String aName) {
            this.aName = aName;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }

        @Override
        public String toString() {
            return "A{" +
                    "aName='" + aName + '\'' +
                    ", b=" + b +
                    '}';
        }
    }

    static class B{
        private String nName;

        public B(String nName) {
            this.nName = nName;
        }

        @Override
        public String toString() {
            return "B{" +
                    "nName='" + nName + '\'' +
                    '}';
        }
    }

}
