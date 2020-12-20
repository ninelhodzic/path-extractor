package org.datazup.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortingUtils {

    public static List sortList(List list, final String finalDirection, final String finalSortingComponent){
        if (null==list || list.size()==0)
            return list;

        // resolved List to be sorted
        List sortedList = list;
        // check first item to discover the type - we assume all items are of the same type.
        Object firstVal = list.get(0);
        if (firstVal instanceof Map){

            Collections.sort(list, (e1, e2)->{
                Map<Object, Object> m1 = (Map)e1;
                Map<Object, Object> m2 = (Map)e2;

                Map.Entry<Object, Object> o1 = m1.entrySet().iterator().next();
                Map.Entry<Object, Object> o2 = m2.entrySet().iterator().next();

                Object key1 = o1.getKey();
                Object val1 =o1.getValue();

                Object key2 = o2.getKey();
                Object val2 = o2.getValue();


                if (finalDirection.equalsIgnoreCase("ASC")){
                    if (finalSortingComponent.equalsIgnoreCase("BY_KEY")){
                        return compare(key2, key1);
                    }else{
                        return compare(val2, val1);
                    }
                }else{
                    if (finalSortingComponent.equalsIgnoreCase("BY_KEY")){
                        return compare(key1, key2);
                    }else{
                        return compare(val1, val2);
                    }
                }
            });
            sortedList = list;
        }else {
            if (finalDirection.equalsIgnoreCase("DESC"))
                list.sort(Collections.reverseOrder());
            else
                Collections.sort(list);
            sortedList = list;
        }

        return sortedList;
    }

    public static Map sortMap(Map<Object, Object> map, final String finalDirection, final String finalSortingComponent){

        if (null==map || map.size()==0)
            return map;

        Map sortedMap = null;

        // resolved Map to be sorted
        if (finalSortingComponent.equalsIgnoreCase("BY_KEY")){
            if (finalDirection.equalsIgnoreCase("ASC")){
                sortedMap = map.entrySet().stream().sorted((e1, e2)->{
                    return compare(e1.getKey(), e2.getKey());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)->e1, LinkedHashMap::new));
            }else{
                sortedMap = map.entrySet().stream().sorted((e1, e2)->{
                    return compare(e2.getKey(), e1.getKey());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)->e1, LinkedHashMap::new));
            }
        }else{
            if (finalDirection.equalsIgnoreCase("ASC")){
                sortedMap = map.entrySet().stream().sorted((e1, e2)->{
                    return compare(e1.getValue(), e2.getValue());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)->e1, LinkedHashMap::new));
            }else{
                sortedMap = map.entrySet().stream().sorted((e1, e2)->{
                    return compare(e2.getValue(), e1.getValue());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)->e1, LinkedHashMap::new));
            }
        }

        return sortedMap;
    }

    public static int compare(Object key1, Object key2){
        if (key1 instanceof Number){
            return Double.compare(((Number) key1).doubleValue(), ((Number) key2).doubleValue());
        }else if (key1 instanceof Boolean){
            return Boolean.compare((Boolean)key1,  (Boolean)key2);
        }else if (key1 instanceof Character){
            return Character.compare((Character)key1, (Character)key2);
        }else if (key1 instanceof String){
            return ((String)key1).compareTo((String)key2);
        }else if (key1 instanceof Byte){
            return Byte.compare((Byte)key1, (Byte)key2);
        }else{
            return key1.toString().compareTo(key2.toString());
        }
    }


}
