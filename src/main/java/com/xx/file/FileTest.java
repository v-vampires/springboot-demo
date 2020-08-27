package com.xx.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yifanl
 * @Date 2020/6/24 14:19
 */
public class FileTest {


    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readLines(new File("D:\\result.txt"), Charset.forName("utf-8"));
        List<String> lines2 = Files.readLines(new File("D:\\rbac.csv"), Charset.forName("utf-8"));

        lines.forEach(line -> System.out.println(line));
        List<String> upAndDown = Lists.newArrayList();
        LinkedHashMap<String, String> linkedHashMap = Maps.newLinkedHashMap();
        System.out.println(lines.size());
        ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();
        lines.forEach(line ->{
            String[] split = line.split(",");
            multimap.put(split[4], split[2]);
            upAndDown.add(split[2]+"_"+split[4]);
            linkedHashMap.put(split[2]+"_"+split[4], line);
        });
        System.out.println(multimap.size());
        System.out.println(multimap);
        System.out.println(upAndDown);
        Map<String, String> names = Maps.newHashMap();
        lines2.forEach(line ->{
            String[] split = line.split(",");
            names.put(split[0], split[1]);
        });

        for (String s : upAndDown) {
            String[] ss = s.split("_");
            String up = ss[0];
            String down = ss[1];
            List<String> ups = Lists.newArrayList(multimap.get(down));
            boolean needRemove = false;
            if(ups.size()==1){
                needRemove = false;
            }else{
                ups.remove(up);
                needRemove = hasUpper(multimap, ups, up);
            }
            //System.out.println(s + (needRemove ? " needRemove": " not need Remove"));
            if(!needRemove){
                String sql = "update CrmUserExtended set SuperiorUserId=" + up +", SuperiorUserName='" + names.get(up) +"' where AccountUserId="+down+";";
                System.out.println(sql);
            }
        }
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
