package com.alerter.javulator.pathextractor;

import org.datazup.pathextractor.PathExtractor;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ninel on 11/24/16.
 */
public class ExtractorTestBase {

    public Map<String,Object> getData(){
        Map<String,Object> child = new HashMap<>();
        child.put("name","child");
        child.put("value", 1);

        Map<String , Object> parent = new HashMap<>();

        List<Object> list = new ArrayList<>();
        list.add("Hello");
        list.add("Hi");
        list.add("Nice");

        Map<String,Object> mp = new HashMap<>();
        mp.put("first",1);
        mp.put("second","hah");

        Map<String,Object> cmp = new HashMap<>();
        cmp.put("thirdChild","yupi");

        List<Object> ll = new ArrayList<>();
        ll.add("thirdhaha");
        ll.add("thirdopa");
        ll.add("thirdjope");
        cmp.put("thirdlist",ll);

        mp.put("third",cmp);

        List<Object> l = new ArrayList<>();
        l.add("childHello");
        l.add("child2Hello");
        l.add("child3Hello");
        mp.put("fourth",l);
        list.add(mp);

        List<Object> list1 = new ArrayList<>();
        list1.add((new HashMap<>().put("n", "n")));

        child.put("list",list);


        parent.put("child", child);
        parent.put("list",list1);
        return parent;
    }

    protected PathExtractor pathExtractor;

    @Before
    public void init(){
        pathExtractor = new PathExtractor(getData());
    }
}
