package org.datazup.pathextractor;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.datazup.template.engine.HandlerBarRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ninel on 7/23/16.
 */
public abstract class PathExtractorBase implements AbstractVariableSet {

    HandlerBarRenderer handlerBarRenderer = new HandlerBarRenderer();

    public abstract Object extractObjectValue(String path);
    public abstract Map resolveToMap(Object o);
    public abstract List resolveToList(Object o);
    public abstract Map<String,Object> getDataObject();

    public List<Object> extractObjectList(String listChildPath) {
        return (List<Object>) extractObjectValue(listChildPath);
    }

    public Object extractObjectValue(Map<String, Object> objMap, String path) {
        return extractObjectValue(objMap, path, false, false);
    }

    public Object extractObjectValue(Map<String, Object> objMap, String path, boolean shouldRemove, boolean returnRowMap) {
        if (null == objMap || null == path || path.isEmpty()) return objMap;

        path = normalizePath(path);

        String tmp = path;

        if (path.contains(".")) {
            String key = path.substring(0, path.indexOf("."));
            String rest = path.substring(path.indexOf(".") + 1, path.length());
            if (objMap.containsKey(key)) {
                Object keyObj = objMap.get(key);

                Map resolvedMap = resolveToMap(keyObj);
                Object obj = extractObjectValue(resolvedMap, rest, shouldRemove, returnRowMap);
                return obj;
                /*
                if (keyObj instanceof Map) {
                    Object obj = extractObjectValue((Map) keyObj, rest, shouldRemove, returnRowMap);
                    return obj;
                } else if (keyObj instanceof JsonObject) {
                    JsonObject jsonObject = (JsonObject) keyObj;
                    Object obj = extractObjectValue(jsonObject.getMap(), rest, shouldRemove, returnRowMap);
                    return obj;
                } else {
                    // do we need to throw error?
                }*/
            } else if (key.endsWith("]")) {
                return handleListParenthesisExtraction(objMap, path, shouldRemove, returnRowMap);
            }
        } else {
            if (path.endsWith("]")) {
                return handleListParenthesisExtraction(objMap, path, shouldRemove, returnRowMap);
            }
            if (objMap.containsKey(tmp)) {
                if (returnRowMap) {
                    return objMap;
                } else {
                    if (shouldRemove) {
                        return objMap.remove(tmp);
                    } else {
                        return objMap.get(tmp);
                    }
                }
            }
        }
        /**/
        return null;
    }

    private Object handleListParenthesisExtraction(Map<String, Object> objMap, String path, boolean shouldRemove, boolean returnRowMap) {
        if (path.contains("]")) {
            String listKey = path.substring(0, path.indexOf("["));
            String parameter = path.substring(path.indexOf("[") + 1, path.indexOf("]"));
            String rest = path.substring(path.indexOf("]") + 1, path.length());
            if (rest.startsWith(".")) {
                rest = rest.substring(1, rest.length());
            }
            // we need to escape '.' dot after ] (sample is: list[0].item
            if (objMap.containsKey(listKey)) {
                Object listObj = objMap.get(listKey);
                List list = resolveToList(listObj);

                Object res = handleReturnFromList(list, objMap, listKey, parameter, rest, shouldRemove, returnRowMap);
                return res;

                /*if (listObj instanceof List) {
                    List list = (List) listObj;
                    Object res = handleReturnFromList(list, objMap, listKey, parameter, rest, shouldRemove, returnRowMap);
                    return res;
                } else if (listObj instanceof JsonArray) {
                    JsonArray list = (JsonArray) listObj;
                    List lst = list.getList();
                    Object res = handleReturnFromList(lst, objMap, listKey, parameter, rest, shouldRemove, returnRowMap);
                    return res;
                }*/
            }
        }
        return null;
    }

    private Object handleReturnFromList(List list, Map<String, Object> objMap, String listKey, String parameter, String rest, boolean shouldRemove, boolean returnRowMap) {
        Integer index = null;
        if (StringUtils.isNotEmpty(parameter)) {
            index = getListIndex(parameter, list.size());
            if (null != index) {
                Object itemFromList = list.get(index);
                if (null != itemFromList) {

                    Object obj = resolveToMap(itemFromList);
                    if (obj instanceof Map){
                        return extractObjectValue((Map) obj, rest, shouldRemove, returnRowMap);
                    }else {
                        obj = resolveToList(itemFromList);
                        if (obj instanceof List) {
                            Object o = extractFieldValues((List) obj, rest, shouldRemove, returnRowMap);
                            return o;
                        }
                    }

                  /*  if (itemFromList instanceof Map) {
                        return extractObjectValue((Map) itemFromList, rest, shouldRemove, returnRowMap);
                    } else if (itemFromList instanceof JsonObject) {
                        return extractObjectValue(((JsonObject) itemFromList).getMap(), rest, shouldRemove, returnRowMap);
                    } else if (itemFromList instanceof List) {
                        Object o = extractFieldValues((List) itemFromList, rest, shouldRemove, returnRowMap);
                        return o;
                    } else if (itemFromList instanceof JsonArray) {
                        JsonArray jsonArray = (JsonArray) itemFromList;
                        Object o = extractFieldValues(jsonArray.getList(), rest, shouldRemove, returnRowMap);
                        return o;
                    }*/
                } else {
                    if (returnRowMap) {
                        return list;
                    } else {
                        return itemFromList;
                    }

                }
            }
        }


        if (StringUtils.isNotEmpty(rest)) {
            // extract list of single object based on  rest value
            Object listOfObjects = extractFieldValues(list, rest, shouldRemove, returnRowMap);
            return listOfObjects;

        } else {
            if (shouldRemove) {
                if (null==index){
                    return null;
                }else{
                    Object removed = list.remove(index.intValue());
                    return removed;
                }
            } else {
                return list;
            }
        }
        // return null;
    }

    private Object extractFieldValues(List<Map<String, Object>> list, String rest, boolean shouldRemove, boolean returnRowMap) {
        List<Object> listOfObjects = new ArrayList<>();
        if (StringUtils.isEmpty(rest)) {
            return list;
        }
        for (Map<String, Object> map : list) {
            /*Object extracted = extractObjectValue(map,rest,shouldRemove);
            listOfObjects.add(extracted);*/
            if (rest.contentEquals(".") || rest.contains("]")) {
                // need to extract further
                Object restObj = extractObjectValue(map, rest, shouldRemove, returnRowMap);
                if (null != restObj) {

                    Object obj = resolveToMap(restObj);
                    if (null!=obj && obj instanceof Map){
                        Map m = (Map) restObj;
                        if (m.containsKey(rest) && null != m.get(rest)) {
                            listOfObjects.add(restObj);
                        }
                    }else {
                        obj = resolveToList(restObj);
                        if (null != obj && obj instanceof List) {
                            return restObj;
                        }
                    }

                    /*if (restObj instanceof Map) {
                        Map m = (Map) restObj;
                        if (m.containsKey(rest) && null != m.get(rest)) {
                            listOfObjects.add(restObj);
                        }
                    } else if (restObj instanceof JsonObject) {
                        Map m = ((JsonObject) restObj).getMap();
                        if (m.containsKey(rest) && null != m.get(rest)) {
                            listOfObjects.add(m);
                        }
                    } else if (restObj instanceof List) {
                        return restObj;
                    } else if (restObj instanceof JsonArray) {
                        return ((JsonArray) restObj).getList();
                    }*/
                }
            } else {
                if (map.containsKey(rest)) {
                    listOfObjects.add(map);
                }
            }
        }
        return listOfObjects;
    }

    private Integer getListIndex(String parameter, Integer listSize) {
        Integer index = null;
        if (parameter.equalsIgnoreCase("last")) {
            index = listSize - 1;
        } else if (NumberUtils.isNumber(parameter)) {
            index = NumberUtils.createInteger(parameter);
        }
        return index;
    }

    public String normalizePath(String path) {
        if (null==path || path.isEmpty()) return path;

        if (path.startsWith("$") && path.endsWith("$")) {
            path = path.substring(1, path.length() - 1);
        }
        return path;
    }


    public Object compileString(String dataItem) throws IOException {

        if (dataItem.contains(Handlebars.DELIM_START) && dataItem.contains(Handlebars.DELIM_END)) {
            return renderTemplate(dataItem);
        }else{
            Object o = extractObjectValue(dataItem);
            return o;

               /*
            if (o instanceof String){
                return o;
            }else{
                Object obj = resolveToMap(o);
                if (null!=obj && obj instanceof Map)
                    return obj;
                obj = resolveToList(o);
                if (null!=obj && obj instanceof List){
                    return obj;
                }
                return JsonUtils.getJsonFromObject(o);


            }

*/

            /*if (o instanceof String){
                return (String)o;
            }else if (o instanceof JsonObject || o instanceof JsonArray){
                return o;
            }else if (o instanceof Map){
                return new JsonObject((Map)o);
            }else if (o instanceof List){
                return new JsonArray((List)o);
            }else{
                return JsonUtils.getJsonFromObject(o);
            }*/
        }

    }

    public String renderTemplate(String item) throws IOException {

        Template tmpl = handlerBarRenderer.getNext().compileInline(item);
        String alertSubjectResult = tmpl.apply(getDataObject());
        return alertSubjectResult;

    }



    /*protected String getLastArgumentFieldName(String argumentFullPath) {
        String normalized = normalizePath(argumentFullPath);
        String valueKeyInList = normalized;
        if (normalized.contains(".")) {
            valueKeyInList = normalized.substring(normalized.lastIndexOf(".") + 1);
            if (valueKeyInList.contains("]")) {
                valueKeyInList = valueKeyInList.substring(valueKeyInList.lastIndexOf("]"));
                if (StringUtils.isEmpty(valueKeyInList)) {
                    valueKeyInList = valueKeyInList.substring(0, valueKeyInList.indexOf("["));
                }
            }
        }
        return valueKeyInList;
    }*/
}
