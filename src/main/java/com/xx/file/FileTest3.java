package com.xx.file;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yifanl
 * @Date 2020/6/24 14:19
 */
public class FileTest3 {


    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> map = Maps.newHashMap();
        map.put(1,100);
        map.put(303,1);
        System.out.println(JSON.toJSONString(map));
        String s = "{1:100,303:1}";
        Map<Integer, Integer> map1 = JSON.parseObject("", Map.class);
        System.out.println(map1);
        System.out.println(map1.get(1));
    }

    private static boolean hasUpper(ArrayListMultimap<String, String> multimap, List<String> ups, String targetUp) {
        if(ups.contains(targetUp)){
            return true;
        }
        for (String up : ups) {
            List<String> childUps = multimap.get(up);
            return hasUpper(multimap, childUps, targetUp);
        }
        return false;
    }



}
