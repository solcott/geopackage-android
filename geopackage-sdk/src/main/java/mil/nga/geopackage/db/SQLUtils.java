package mil.nga.geopackage.db;

import android.content.ContentValues;

import java.util.Map;

/**
 * Core SQL Utility methods
 *
 * @author osbornb
 * @since 1.3.1
 */
public class SQLUtils {

    /**
     * Wrap the content values names in quotes
     *
     * @param values content values
     * @return quoted content values
     */
    public static ContentValues quoteWrap(ContentValues values) {
        ContentValues quoteValues = null;
        if (values != null) {
            quoteValues = new ContentValues();
            for (Map.Entry<String, Object> valueSet : values.valueSet()) {
                String key = CoreSQLUtils.quoteWrap(valueSet.getKey());
                Object value = valueSet.getValue();
                if (value instanceof String) {
                    quoteValues.put(key, (String) value);
                } else if (value == null) {
                    quoteValues.putNull(key);
                } else {
                    Class<?> primitiveType = primitiveType(value.getClass());
                    if (primitiveType == Integer.TYPE) {
                        quoteValues.put(key, (Integer) value);
                    } else if (primitiveType == Byte.TYPE) {
                        quoteValues.put(key, (Byte) value);
                    } else if (primitiveType == Long.TYPE) {
                        quoteValues.put(key, (Long) value);
                    } else if (primitiveType == Float.TYPE) {
                        quoteValues.put(key, (Float) value);
                    } else if (primitiveType == Double.TYPE) {
                        quoteValues.put(key, (Double) value);
                    } else if (primitiveType == Short.TYPE) {
                        quoteValues.put(key, (Short) value);
                    } else if (primitiveType == Boolean.TYPE) {
                        quoteValues.put(key, (Boolean) value);
                    } else {
                        quoteValues.put(key, (byte[])value);
                    }
                }
            }

        }

        return quoteValues;
    }

    /**
     * Method that can be used to find primitive type for given class if (but only if)
     * it is either wrapper type or primitive type; returns `null` if type is neither.
     *
     * @since 2.7
     */
    private static Class<?> primitiveType(Class<?> type) {
        if (type.isPrimitive()) {
            return type;
        }

        if (type == Integer.class) {
            return Integer.TYPE;
        }
        if (type == Long.class) {
            return Long.TYPE;
        }
        if (type == Boolean.class) {
            return Boolean.TYPE;
        }
        if (type == Double.class) {
            return Double.TYPE;
        }
        if (type == Float.class) {
            return Float.TYPE;
        }
        if (type == Byte.class) {
            return Byte.TYPE;
        }
        if (type == Short.class) {
            return Short.TYPE;
        }
        if (type == Character.class) {
            return Character.TYPE;
        }
        return null;
    }

}
