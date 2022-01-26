package org.datazup.pathextractor;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.datazup.exceptions.PathExtractorException;
import org.datazup.template.engine.HandlerBarRenderer;
import org.datazup.utils.TypeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin@datazup on 7/23/16.
 */
public abstract class PathExtractorBase implements AbstractVariableSet {

    HandlerBarRenderer handlerBarRenderer;

    public abstract Object extractObjectValue(String path);

    //public abstract Map resolveToMap(Object o);
    //public abstract List resolveToList(Object o);
    public abstract Map<String, Object> getDataObject();

    public abstract Map<String, Helper> handlebarsHelpers();

    //public abstract Map resolveDeepMap(Map dataObject);
    private AbstractResolverHelper mapListResolver;

    public PathExtractorBase(){

    }

    protected void init(){
        handlerBarRenderer  = new HandlerBarRenderer();
        if (null!=handlebarsHelpers()){
            for(String name: handlebarsHelpers().keySet()) {
                handlerBarRenderer.registerHelper(name, handlebarsHelpers().get(name));
            }
        }
        handlerBarRenderer.init();
    }

    public Object extractObjectValue(Map<String, Object> objMap, String path) {
        if (path.contains(Handlebars.DELIM_START) && path.contains(Handlebars.DELIM_END)) {
            try {
                Object o = compileString(objMap, path);
                return o;
            } catch (IOException e) {
                throw new PathExtractorException("Error in compile string with path: " + path, e);
            }
        }
        return extractObjectValue(objMap, path, false, false);
    }

    public void update(Map<String, Object> objMap, String path, Object value) {
        path = normalizePath(path);
        // find field and update the data - last part can be: fieldName, indexInList, lastItemInList
        if (path.contains(".")) {

            String key = path.substring(0, path.indexOf("."));
            String rest = path.substring(path.indexOf(".") + 1);
            if (objMap.containsKey(key)) {
                Object keyObj = objMap.get(key);
                Map resolvedMap = mapListResolver.resolveToMap(keyObj);
                update(resolvedMap, rest, value);
                return;
            } else if (key.endsWith("]")) {
                updateExtractedParenthesisList(objMap, path, value);
                return;
            }
        } else if (path.contains("]")) {
            updateExtractedParenthesisList(objMap, path, value);
            return;
        } else {
            objMap.put(path, value);
        }
    }


    public Object extractObjectValue(Map<String, Object> objMap, String path, boolean shouldRemove, boolean returnRowMap) {
        if (null == objMap || null == path || path.isEmpty()) return objMap;

        try {
            path = normalizePath(path);

        } catch (Throwable e) {
            throw new PathExtractorException("Error normalizing Path: " + path, e);
        }
        String tmp = path;

        if (path.contains(".")) {
            String key = null;
            String rest = null;
            try {
                key = path.substring(0, path.indexOf("."));
                rest = path.substring(path.indexOf(".") + 1);
            } catch (Throwable e) {
                throw new PathExtractorException("Cannot process dot notation for path: " + path, e);
            }
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
            String rest = path.substring(path.indexOf("]") + 1);
            if (rest.startsWith(".")) {
                rest = rest.substring(1);
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
        if (null != list) {
            Integer index = null;
            if (StringUtils.isNotEmpty(parameter)) {
                index = getListIndex(parameter, list.size());
                if (null != index) {
                    if (StringUtils.isEmpty(rest)) {
                        list.set(index, value);
                    } else if (!StringUtils.isEmpty(listKey)) {
                        Object itemFromList = list.get(index);
                        if (null != itemFromList) {
                            Object obj = mapListResolver.resolveToMap(itemFromList);
                            if (obj instanceof Map) {
                                update((Map<String, Object>) obj, rest, value);
                            } else {
                                obj = mapListResolver.resolveToList(itemFromList);
                                if (obj instanceof List) {
                                    updateExtractedParenthesisList(objMap, rest, value);
                                }
                            }
                        }
                    } else {
                        // is there such case???
                    }
                }
            } else {
                if (StringUtils.isEmpty(rest)) {
                    objMap.put(listKey, value);
                }
            }
        }
    }

    private Object handleListParenthesisExtraction(Map<String, Object> objMap, String path, boolean shouldRemove, boolean returnRowMap) {
        if (path.contains("]")) {

            try {
                String listKey = path.substring(0, path.indexOf("["));
                String parameter = path.substring(path.indexOf("[") + 1, path.indexOf("]"));
                String rest = path.substring(path.indexOf("]") + 1);
                if (rest.startsWith(".")) {
                    rest = rest.substring(1);
                }
                // we need to escape '.' dot after ] (sample is: list[0].item
                if (objMap.containsKey(listKey)) {
                    Object listObj = objMap.get(listKey);
                    List list = mapListResolver.resolveToList(listObj);

                    Object res = handleReturnFromList(list, objMap, listKey, parameter, rest, shouldRemove, returnRowMap);
                    return res;
                }
            } catch (Throwable e) {
                throw new PathExtractorException("Error processing list parenthesis inpath: " + path, e);
            }
        }
        return null;
    }

    private Object handleReturnFromList(List list, Map<String, Object> objMap, String listKey, String parameter, String rest, boolean shouldRemove, boolean returnRowMap) {
        Integer index = null;
        if (null == list)
            return null;

        if (StringUtils.isNotEmpty(parameter)) {
            index = getListIndex(parameter, list.size());
            if (null != index) {
                Object itemFromList = null;
                if (list.size() > index) {
                    itemFromList = list.get(index);
                } else {
                    return itemFromList;
                }

                if (StringUtils.isEmpty(rest) && shouldRemove) {
                    return list.remove(index.intValue());
                } else if (null != itemFromList) {

                    Object obj = mapListResolver.resolveToMap(itemFromList);
                    if (obj instanceof Map) {
                        return extractObjectValue((Map) obj, rest, shouldRemove, returnRowMap);
                    } else {
                        obj = mapListResolver.resolveToList(itemFromList);
                        if (obj instanceof List) {
                            Object o = extractFieldValues((List) obj, rest, shouldRemove, returnRowMap);
                            return o;
                        } else {
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
            Object listOfObjects = extractFieldValues(list, rest, shouldRemove, returnRowMap);
            return listOfObjects;
        } else {
            if (shouldRemove) {
                if (null == index) {
                    return null;
                } else {
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
        if (list.size() == 0)
            return null;

        Iterator<Object> iter = list.iterator();

        while (iter.hasNext()) {
            Object objInList = iter.next();

            if (null != rest && !rest.isEmpty()) {

                Map<String, Object> map = mapListResolver.resolveToMap(objInList);

                if (rest.contains(".") || rest.contains("]")) {
                    // need to extract further
                    Object restObj = extractObjectValue(map, rest, shouldRemove, returnRowMap);
                    if (null != restObj) {
                        listOfObjects.add(restObj);
                    }
                } else {
                    if (map.containsKey(rest)) {
                        Object o = map.get(rest);
                        listOfObjects.add(o);
                    }
                }
            } else {
                listOfObjects.add(objInList);
            }
        }

        return listOfObjects;
    }

    private Integer getListIndex(String parameter, Integer listSize) {
        Integer index = null;

        if (parameter.startsWith("'#") && parameter.endsWith("#'")) {
            String param = parameter.substring(2, parameter.length() - 2);
            Object o = extractObjectValue(param);
            index = TypeUtils.resolveInteger(o);
        } else if (parameter.startsWith("$") && parameter.endsWith("$")) {
            Object o = extractObjectValue(parameter);
            index = TypeUtils.resolveInteger(o);
        } else if (parameter.equalsIgnoreCase("last")) {
            index = listSize - 1;
        } else if (NumberUtils.isCreatable(parameter)) {
            index = NumberUtils.createInteger(parameter);
        }
        return index;
    }

    public static String normalizePath(String path) {
        if (null == path || path.isEmpty()) return path;

        if (path.startsWith("$") && path.endsWith("$")) {
            path = path.substring(1, path.length() - 1);
        }

        if (path.startsWith("$") && path.length() > 1 && !Character.isDigit(path.charAt(1)) && !path.endsWith("$")) {
            throw new PathExtractorException("Path starts with $ but it doesn't end with $: " + path);
        }
        return path;
    }


    public Object compileString(String expression) throws IOException {
        Map<String, Object> map = getDataObject();
        return compileString(map, expression);
    }

    public Object compileString(Map<String, Object> dataObject, String expression) throws IOException {

        if (expression.contains(Handlebars.DELIM_START) && expression.contains(Handlebars.DELIM_END)) {
            Map<String, Object> map = mapListResolver.resolveDeepMap(dataObject);
            return renderTemplate(map, expression);
        } else {
            Object o = extractObjectValue(dataObject, expression);
            if (null != o)
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

    public String renderTemplate(Map<String, Object> dataObject, String item) throws IOException {

        Template tmpl = handlerBarRenderer.getNext().compileInline(item);

        String alertSubjectResult = tmpl.apply(dataObject);
        return alertSubjectResult;

    }

    public AbstractResolverHelper getMapListResolver() {
        return mapListResolver;
    }

    public void setMapListResolver(AbstractResolverHelper mapListResolver) {
        this.mapListResolver = mapListResolver;
    }
}
