package org.datazup.pathextractor;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.datazup.template.engine.HandlerBarRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ninel on 7/23/16.
 */
public abstract class PathExtractorBase implements AbstractVariableSet {

    HandlerBarRenderer handlerBarRenderer = new HandlerBarRenderer();

    public abstract Object extractObjectValue(String path);
    //public abstract Map resolveToMap(Object o);
    //public abstract List resolveToList(Object o);
    public abstract Map<String,Object> getDataObject();
    //public abstract Map resolveDeepMap(Map dataObject);
    private AbstractMapListResolver mapListResolver;


    public Object extractObjectValue(Map<String, Object> objMap, String path) {
        if (path.contains(Handlebars.DELIM_START) && path.contains(Handlebars.DELIM_END)) {
            try {
                Object o = compileString(objMap, path);
                return o;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return extractObjectValue(objMap, path, false, false);
    }

    public void update(Map<String, Object> objMap,String path, Object value) {
        path = normalizePath(path);
        // find field and update the data - last part can be: fieldName, indexInList, lastItemInList
        if (path.contains(".")){

            String key = path.substring(0, path.indexOf("."));
            String rest = path.substring(path.indexOf(".") + 1, path.length());
            if (objMap.containsKey(key)) {
                Object keyObj = objMap.get(key);
                Map resolvedMap = mapListResolver.resolveToMap(keyObj);
                update(resolvedMap, rest, value);
                return;
            }else if (key.endsWith("]")){
                updateExtractedParenthesisList(objMap, path, value);
                return;
            }
        }else if (path.contains("]")){
            updateExtractedParenthesisList(objMap, path, value);
            return;
        }else{
            objMap.put(path, value);
        }
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

                Map resolvedMap = mapListResolver.resolveToMap(keyObj);
                Object obj = extractObjectValue(resolvedMap, rest, shouldRemove, returnRowMap);
                return obj;
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

    private void updateExtractedParenthesisList(Map<String, Object> objMap, String path, Object value) {
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
                List list = mapListResolver.resolveToList(listObj);
                updateInList(list, objMap, listKey, parameter, rest, value);
            }

        }
    }

    private void updateInList(List list, Map<String, Object> objMap, String listKey, String parameter, String rest, Object value) {
        if (null!=list){
            Integer index = null;
            if (StringUtils.isNotEmpty(parameter)) {
                index = getListIndex(parameter, list.size());
                if (null != index) {
                    if (StringUtils.isEmpty(rest)){
                        list.set(index, value);
                    }else if (!StringUtils.isEmpty(listKey)){
                        Object itemFromList = list.get(index);
                        if (null!=itemFromList){
                            Object obj = mapListResolver.resolveToMap(itemFromList);
                            if (obj instanceof Map){
                                update((Map<String, Object>) obj, rest, value);
                            }else{
                                obj = mapListResolver.resolveToList(itemFromList);
                                if (obj instanceof List) {
                                    updateExtractedParenthesisList(objMap, rest, value);
                                }
                            }
                        }
                    }else {
                        // is there such case???
                    }
                }
            }else{
                if (StringUtils.isEmpty(rest)){
                    objMap.put(listKey, value);
                }
            }
        }
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
                List list = mapListResolver.resolveToList(listObj);

                Object res = handleReturnFromList(list, objMap, listKey, parameter, rest, shouldRemove, returnRowMap);
                return res;
            }
        }
        return null;
    }

    private Object handleReturnFromList(List list, Map<String, Object> objMap, String listKey, String parameter, String rest, boolean shouldRemove, boolean returnRowMap) {
        Integer index = null;
        if (StringUtils.isNotEmpty(parameter)) {
            index = getListIndex(parameter, list.size());
            if (null != index) {
                Object itemFromList = null;
                if (list.size()>index){
                    itemFromList = list.get(index);
                }else{
                    return itemFromList;
                }

                if (StringUtils.isEmpty(rest) && shouldRemove){
                    return list.remove(index.intValue());
                }else if (null != itemFromList) {

                    Object obj = mapListResolver.resolveToMap(itemFromList);
                    if (obj instanceof Map){
                        return extractObjectValue((Map) obj, rest, shouldRemove, returnRowMap);
                    }else {
                        obj = mapListResolver.resolveToList(itemFromList);
                        if (obj instanceof List) {
                            Object o = extractFieldValues((List) obj, rest, shouldRemove, returnRowMap);
                            return o;
                        }else{
                            return itemFromList;
                        }
                    }

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

    private Object extractFieldValues(List<Object> list, String rest, boolean shouldRemove, boolean returnRowMap) {
        List<Object> listOfObjects = new ArrayList<>();
        if (StringUtils.isEmpty(rest)) {
            return list;
        }
        if (list.size()==0)
            return null;

        Iterator<Object> iter = list.iterator();

        while(iter.hasNext()){
            Object objInList = iter.next();
            Map<String,Object> map =  mapListResolver.resolveToMap(objInList);

            if (rest.contentEquals(".") || rest.contains("]")) {
                // need to extract further
                Object restObj = extractObjectValue(map, rest, shouldRemove, returnRowMap);
                if (null != restObj) {

                    Object obj = mapListResolver.resolveToMap(restObj);
                    if (null!=obj && obj instanceof Map){
                        Map m = (Map) restObj;
                        if (m.containsKey(rest) && null != m.get(rest)) {
                            listOfObjects.add(restObj);
                        }
                    }else {
                        obj = mapListResolver.resolveToList(restObj);
                        if (null != obj && obj instanceof List) {
                            return restObj;
                        }
                    }
                }
            } else {
                if (map.containsKey(rest)) {
                    Object o = map.get(rest);
                    listOfObjects.add(o);
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

    public static String normalizePath(String path) {
        if (null==path || path.isEmpty()) return path;

        if (path.startsWith("$") && path.endsWith("$")) {
            path = path.substring(1, path.length() - 1);
        }
        return path;
    }


    public Object compileString(String expression) throws IOException {
        Map<String, Object> map = getDataObject();
        return compileString(map, expression);
    }

    public Object compileString(Map<String,Object> dataObject, String expression) throws IOException {

        if (expression.contains(Handlebars.DELIM_START) && expression.contains(Handlebars.DELIM_END)) {
            Map<String, Object> map = mapListResolver.resolveDeepMap(dataObject);
            return renderTemplate(map, expression);
        }else{
            Object o = extractObjectValue(dataObject, expression);
            if (null!=o)
                return o;
            return expression;
        }
    }



    public String renderTemplate(String item) throws IOException {

       /* Template tmpl = handlerBarRenderer.getNext().compileInline(item);
        String alertSubjectResult = tmpl.apply(getDataObject());
        return alertSubjectResult;*/

        return renderTemplate(getDataObject(), item);

    }

    public String renderTemplate(Map<String,Object> dataObject, String item) throws IOException {

        Template tmpl = handlerBarRenderer.getNext().compileInline(item);
        String alertSubjectResult = tmpl.apply(dataObject);
        return alertSubjectResult;

    }

    public AbstractMapListResolver getMapListResolver() {
        return mapListResolver;
    }

    public void setMapListResolver(AbstractMapListResolver mapListResolver) {
        this.mapListResolver = mapListResolver;
    }
}
