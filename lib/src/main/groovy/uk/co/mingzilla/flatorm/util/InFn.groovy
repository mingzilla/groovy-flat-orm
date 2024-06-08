package uk.co.mingzilla.flatorm.util

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode

/**
 * Internal Fn
 */
@CompileStatic
class InFn {

    static Boolean asBoolean(Object obj) {
        obj == null ? obj : asString(obj).trim()?.toLowerCase() == 'true'
    }

    static BigDecimal asBigDecimal(Object obj) {
        String stringVal = "$obj".trim()
        return stringVal.isBigDecimal() ? new BigDecimal(stringVal) : null
    }

    static def asBigDecimalWithScale(Object decimalPlaces, RoundingMode mode, Object obj) {
        Integer decimals = asInteger(decimalPlaces)
        return decimals != null ? asBigDecimal(obj)?.setScale(decimals, mode) : asBigDecimal(obj)
    }

    static Double asDouble(Object obj) {
        String stringVal = "$obj".trim()
        return stringVal.isDouble() ? Double.valueOf(stringVal) : null
    }

    static Double asFloat(Object obj) {
        String stringVal = "$obj".trim()
        return stringVal.isFloat() ? Float.valueOf(stringVal) : null
    }

    static Integer asInteger(Object obj) {
        String stringVal = "$obj".trim()
        return stringVal.isInteger() ? Integer.valueOf(stringVal) : null
    }

    static Long asLong(Object obj) {
        String stringVal = "$obj".trim()
        return stringVal.isLong() ? Long.valueOf(stringVal) : null
    }

    static String asString(Object obj) {
        return obj == null ? null : String.valueOf(obj)
    }

    static <T> T safeGet(T defaultValue, Closure fn) {
        try {
            fn() as T
        } catch (Exception ignore) {
            return defaultValue
        }
    }

    static boolean hasField(String fieldName, Object o) {
        if (o == null) return false
        if (o instanceof Map) return ((Map) o).containsKey(fieldName)
        try {
            o[(fieldName)]
            return true
        } catch (MissingPropertyException ignore) {
            return false
        } catch (IllegalStateException ignore) {
            return false
        }
    }

    static boolean isBigDecimal(Object obj) {
        return asString(obj)?.bigDecimal
    }

    static boolean isBigInteger(Object obj) {
        return asString(obj)?.bigInteger
    }

    static boolean isBoolean(Object obj) {
        StringUtils.trimToEmpty(asString(obj)).toLowerCase() in ['true', 'false']
    }

    static boolean isDouble(Object obj) {
        return asString(obj)?.double
    }

    static boolean isFloat(Object obj) {
        return asString(obj)?.float
    }

    static boolean isInteger(Object obj) {
        return asString(obj)?.integer
    }

    static boolean isLong(Object obj) {
        return asString(obj)?.long
    }

    static boolean isNull(Object v) {
        return v == null || (v != 'null' && v.toString() == 'null') // could be org.codehaus.groovy.grails.web.json.JSONObject$Null
    }

    static boolean isNumber(Object value) {
        value != null && "${value}".isNumber()
    }

    static List<String> getEnumKeys(Class aClass, List<String> customExcludeFields = null) {
        List<String> excludes = ((customExcludeFields ?: []) as List<String>) + ['class', 'declaringClass']
        return (aClass.metaClass.properties*.name - excludes) as List<String>
    }

    static List<String> getKeys(Object o) {
        boolean isEnum = o?.class?.enum
        if (isEnum) {
            return getEnumKeys(o.class)
        } else {
            Map props = o instanceof Map ? (o as Map) : (o?.properties ?: [:])
            return (props.keySet() as List<String>) - ['class']
        }
    }

    static Class getType(Class clazz, String field) {
        try {
            return clazz.getDeclaredField(field).type
        } catch (Exception ignore) {
            return null
        }
    }

    static String camelToUpperSnakeCase(String text) {
        text?.replaceAll(/([A-Z])/, /_$1/)?.toUpperCase()?.replaceAll(/^_/, '')
    }

    static String propAsString(String name, Object obj) {
        return asString(prop(name, obj ?: [:]))
    }

    static String camelToLowerHyphenCase(String text) {
        text?.replaceAll(/([A-Z])/, /-$1/)?.toLowerCase()?.replaceAll(/^-/, '')
    }

    static String hyphenToSnakeCase(String text) {
        text?.replaceAll(/([-])/, '_')
    }

    static String snakeToHyphenCase(String text) {
        text?.replaceAll(/([_])/, '-')
    }

    static Boolean propAsBoolean(String name, Object obj) {
        return asBoolean(propAsString(name, obj))
    }

    static BigDecimal propAsBigDecimal(String name, Object obj) {
        return asBigDecimal(propAsString(name, obj))
    }

    static Double propAsDouble(String name, Object obj) {
        return asDouble(propAsString(name, obj))
    }

    static Float propAsFloat(String name, Object obj) {
        return asFloat(propAsString(name, obj))
    }

    static Integer propAsInteger(String name, Object obj) {
        return asInteger(propAsString(name, obj))
    }

    static Long propAsLong(String name, Object obj) {
        return asLong(propAsString(name, obj))
    }

    static <T> T self(T x) {
        return x
    }

    static Map toMap(Object o, List<String> customExcludeFields = null) {
        List<String> excludeFields = (customExcludeFields ?: [])
        List<String> keys = getKeys(o) - excludeFields

        Map m = [:]

        // evaluating props would trigger running all the get methods, which also executes all the customExcludeFields, loop the keys doesn't do so
        keys.each { String key ->
            m[(key)] = o[(key)]
        }

        // id is not in o.properties, so set it here
        boolean excludeIdField = excludeFields.find { it == 'id' }
        if (!excludeIdField && hasField('id', o)) {
            m['id'] = o['id']
        }

        return m
    }

    static Object prop(String name, Object o) {
        if (name == null) {
            null
        } else if (o instanceof Map) {
            (o as Map)[(name)]
        } else {
            o?.hasProperty(name) ? o[(name)] : null
        }
    }

    static Object setPrimitiveField(Object obj, String k, def v) {
        if (obj == null || k == null) return obj
        Class type = obj.metaClass?.getMetaProperty(k)?.type
        boolean isNull = isNull(v) // could be org.codehaus.groovy.grails.web.json.JSONObject$Null

        try {
            switch (type?.name) {
                case 'int':
                    if (!isNull && isInteger(v)) obj[(k)] = asInteger(v)
                    break
                case 'long':
                    if (!isNull && isLong(v)) obj[(k)] = asLong(v)
                    break
                case 'boolean':
                    if (!isNull && isBoolean(v)) obj[(k)] = asBoolean(v)
                    break
                default:
                    break
            }
            switch (type) {
                case Integer:
                    if (isNull || isInteger(v)) {
                        obj[(k)] = isNull ? null : asInteger(v)
                    }
                    break
                case Long:
                    if (isNull || isLong(v)) obj[(k)] = isNull ? null : asLong(v)
                    break
                case BigDecimal:
                    if (isNull || isBigDecimal(v)) obj[(k)] = isNull ? null : asBigDecimal(v)
                    break
                case Double:
                    if (isNull || isDouble(v)) obj[(k)] = isNull ? null : asDouble(v)
                    break
                case Float:
                    if (isNull || isFloat(v)) obj[(k)] = isNull ? null : asFloat(v)
                    break
                case Boolean:
                    if (isNull || isBoolean(v)) obj[(k)] = isNull ? null : asBoolean(v)
                    break
                case String:
                    obj[(k)] = isNull ? null : v
                    break
                case Date:
                    if (!isNull) obj[(k)] = v
                    break
                default:
                    break
            }
        } catch (ReadOnlyPropertyException ignore) {
            // All the isX() or getX() methods without set methods (e.g. isForData()) would get such an exception.
            // It's not an actual property to be set, so we ignore it
        }
        return obj
    }

    static String spacedToLowerSnakeCase(String text) {
        text?.trim()?.toLowerCase()?.replaceAll(" ", "_")
    }

    static Object trimToEmptyIfIsString(Object v) {
        if (!(v instanceof String)) return v
        if (v == null) return v
        return StringUtils.trimToEmpty(String.valueOf(v))
    }

    static String withoutChar(Object obj) {
        if (obj == null) return obj
        return obj.toString().replaceAll(/[a-zA-Z]+/, '')
    }
}
