package org.datazup.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by admin@datazup on 11/21/17.
 */
public class TypeUtils {
    public static Number resolveNumber(Object o){
        if (o instanceof Number){
            return (Number)o;
        }else if (o instanceof String){
            return NumberUtils.createNumber((String)o);
        }else if (o instanceof Boolean){
            Boolean v = (Boolean)o;
            return v==Boolean.TRUE?1:0;
        }
        return null;
    }

    public static Long resolveLong(Object o){
        Number n = resolveNumber(o);
        if (null!=n){
            return n.longValue();
        }
        return null;
    }
    public static Double resolveDouble(Object o){
        Number n = resolveNumber(o);
        if (null!=n){
            return n.doubleValue();
        }
        return null;
    }
    public static Integer resolveInteger(Object o){
        Number n = resolveNumber(o);
        if (null!=n){
            return n.intValue();
        }
        return null;
    }
    public static Float resolveFloat(Object o){
        Number n = resolveNumber(o);
        if (null!=n){
            return n.floatValue();
        }
        return null;
    }

    public static Boolean resolveBoolean(Object o) {
        if (o instanceof Boolean)
            return (Boolean)o;
        if (o instanceof String){
            return BooleanUtils.toBoolean((String)o);
        }if (o instanceof Number){
            int val = ((Number)o).intValue();
            return BooleanUtils.toBoolean(val);
        }
        return null;
    }

    public static String resolveString(Object o) {
        if (null==o) return null;

        if (o instanceof String){
            return (String)o;
        }
        return o.toString();
    }

    public static Object resolveBestMatching(String stringValue) {
        if (StringUtils.isEmpty(stringValue))
            return stringValue;
        if (NumberUtils.isCreatable(stringValue)){
            Number number = NumberUtils.createNumber(stringValue);
            return number;
        }else{
            if (stringValue.equalsIgnoreCase("true")){
                return Boolean.TRUE;
            }else if (stringValue.equalsIgnoreCase("false")){
                return Boolean.FALSE;
            }else{
                return stringValue;
            }
        }
    }
}
