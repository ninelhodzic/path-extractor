package org.datazup.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.UnsupportedEncodingException;
import java.time.Instant;

/**
 * Created by admin@datazup on 11/21/17.
 */
public class TypeUtils {
    public static Number resolveNumber(Object o) {

        if (o instanceof Number) {
            return (Number) o;
        } else if (o instanceof String) {
            if (!NumberUtils.isCreatable((String) o)) {
                return null;
            }
            return NumberUtils.createNumber((String) o);
        } else if (o instanceof Boolean) {
            Boolean v = (Boolean) o;
            return v == Boolean.TRUE ? 1 : 0;
        } else if (o instanceof Instant) {
            Instant instant = (Instant) o;
            return instant.getEpochSecond();
        }
        return null;
    }

    public static Long resolveLong(Object o) {
        Number n = resolveNumber(o);
        if (null != n) {
            return n.longValue();
        }
        return null;
    }

    public static Long resolveLong(Object o, Long defaultValue) {
        Number n = resolveNumber(o);
        if (null != n) {
            return n.longValue();
        }
        return defaultValue;
    }

    public static Double resolveDouble(Object o, Double defaultValue) {
        Number n = resolveNumber(o);
        if (null != n) {
            return n.doubleValue();
        }
        return defaultValue;
    }

    public static Double resolveDouble(Object o) {
        return resolveDouble(o, null);
    }

    public static Integer resolveInteger(Object o) {
        return resolveInteger(o, null);
    }

    public static Integer resolveInteger(Object o, Integer defaultValue) {
        Number n = resolveNumber(o);
        if (null != n) {
            return n.intValue();
        }
        return defaultValue;
    }

    public static Float resolveFloat(Object o) {
        Number n = resolveNumber(o);
        if (null != n) {
            return n.floatValue();
        }
        return null;
    }

    public static Boolean resolveBoolean(Object o) {
        return resolveBoolean(o, null);
    }

    public static Boolean resolveBoolean(Object o, Boolean defaultVal) {
        if (o instanceof Boolean)
            return (Boolean) o;
        if (o instanceof String) {
            return BooleanUtils.toBoolean((String) o);
        }
        if (o instanceof Number) {
            int val = ((Number) o).intValue();
            return BooleanUtils.toBoolean(val);
        }
        return defaultVal;
    }

    public static String resolveString(Object o, String charset) {
        if (null == o) return null;
        if (null==charset || charset.isEmpty()){
            charset = "UTF-8";
        }
        if (o instanceof String) {
            return (String) o;
        }else if (o instanceof byte[]){
            try {
                return new String((byte[]) o, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
        return o.toString();
    }
    public static String resolveString(Object o) {
        return resolveString(o, "UTF-8");
    }

    public static Object resolveBestMatching(String stringValue) {
        if (StringUtils.isEmpty(stringValue))
            return stringValue;
        if (NumberUtils.isCreatable(stringValue)) {
            Number number = NumberUtils.createNumber(stringValue);
            return number;
        } else {
            if (stringValue.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (stringValue.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else {
                return stringValue;
            }
        }
    }

    public static Object resolveObjectByType(String type, Object obj) {
        Object res = obj;
        String typeU = type.toUpperCase();
        switch (typeU) {
            case "BOOLEAN":
            case "BOOL":
                res = resolveBoolean(obj);
                break;
            case "INTEGER":
            case "INT":
                res = resolveInteger(obj);
                break;
            case "DOUBLE":
                res = resolveDouble(obj);
                break;
            case "LONG":
                res = resolveLong(obj);
                break;
            case "DATE":
            case "DATETIME":
                res = resolveInstant(obj);
                break;
            default:
                res = resolveString(obj);
        }

        return res;
    }

    public static Instant resolveInstant(Object obj) {
        return DateTimeUtils.resolve(obj);
    }

    public static boolean isSimpleVal(Object valObj) {
        return valObj instanceof Number || valObj instanceof Boolean || valObj instanceof String;
    }

    public static String resolveAsText(Object valObj) {
        return valObj.toString();
    }
}
