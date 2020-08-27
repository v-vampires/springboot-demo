package com.xx.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author yifanl
 * @Date 2020/6/24 14:19
 */
public class FileTest2 {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readLines(new File("D:\\result.txt"), Charset.forName("utf-8"));

        lines.forEach(line -> System.out.println(line));
        List<String> upAndDown = Lists.newArrayList();
        System.out.println(lines.size());
        ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();
        lines.forEach(line ->{
            String[] split = line.split(",");
            multimap.put(split[2], split[4]);
            upAndDown.add(split[2]+"_"+split[4]);
        });
        System.out.println(multimap.size());
        System.out.println(multimap);
        System.out.println(upAndDown);

        for (String key : multimap.keys()) {

        }
    }

    @Data
    static class TreeNode{
        String value;
        List<TreeNode> children;
    }

}
