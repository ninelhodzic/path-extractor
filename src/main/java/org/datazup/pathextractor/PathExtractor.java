package org.datazup.pathextractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ninel on 3/13/16.
 */
public class PathExtractor extends PathExtractorBase {
    protected Map<String, Object> objectMap;

    public PathExtractor(){}
    public PathExtractor(Map<String, Object> objectMap, AbstractMapListResolver mapListResolver) {
          this.objectMap = objectMap;
          this.setMapListResolver(mapListResolver);
    }

  /*  public Map<String, Object> getObjectMap() {
        return objectMap;
    }*/

    @Override
    public boolean containsKey(String str) {
        if (null == this.objectMap) return false;
        return this.objectMap.containsKey(str);
    }

    @Override
    public Object get(String var1) {
        return extractObjectValue(var1);
    }

    public Object extractObjectValue(String path) {
        return extractObjectValue(objectMap, path);
    }

    @Override
    public Map<String, Object> getDataObject() {
        return this.objectMap;
    }

    @Override
    public void set(String key, Object value) {
        if (null != this.objectMap) {
            this.objectMap.put(key, value);
        }
    }

    @Override
    public Object remove(String path) {
        return extractObjectValue(this.objectMap, path, true, false);
    }

    public Map<String, Object> extractAndRemoveObjects(List<String> fields, Map<String, Object> inputMap) {
        return extractObjects(fields, inputMap, true);
    }

    public Map<String, Object> extractObjects(List<String> fields, Map<String, Object> inputMap, boolean shouldRemove) {
        if (null == inputMap) return null;

        Map<String, Object> extractedMap = new HashMap<>();

        for (String field: fields){
           Object val = extractObjectValue(inputMap,field, shouldRemove, false);
            String normalizedField = normalizePath(field);
            extractedMap.put(normalizedField, val);
        }
        return extractedMap;
    }

    public void update(String fixedFieldName, Object result){
        update(this.objectMap, fixedFieldName, result);
    }

}
