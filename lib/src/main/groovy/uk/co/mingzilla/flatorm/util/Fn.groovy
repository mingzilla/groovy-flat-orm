package uk.co.mingzilla.flatorm.util

import groovy.json.JsonOutput
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
class Fn {

    // http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
    static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<Class<?>>([
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            BigDecimal.class,
            Float.class,
            Double.class,
            Void.class,
            String.class,
    ])

    static final Set<Class<?>> NON_CUSTOM_OBJECT_TYPES = PRIMITIVE_TYPES + Date.class

    static boolean isNumber = { def value ->
        value != null && "${value}".isNumber()
    }

    static Closure<Boolean> isArray = { def value ->
        return Fn.safeGet(false)({ value.class.array }) as boolean
    }

    static String asString = { def obj ->
        obj == null ? null : String.valueOf(obj)
    }

    static Closure<Closure<String>> asStringWithMaxLength = { Integer maxLength ->
        return { def obj ->
            String value = Fn.asString(obj)
            if (!value) return value
            if (value.length() <= maxLength) return value
            return value.substring(0, maxLength)
        }
    }

    static def propAsString = { String name ->
        { def obj ->
            Fn.asString(Fn.prop(name)(obj ?: [:]))
        }
    }

    static Closure<Closure<String>> nestedPropAsString = { List<String> path ->
        { def obj ->
            Fn.asString(Fn.getIn(path)(obj ?: [:]))
        }
    }

    static def propAsUpperString = { String name ->
        { def obj ->
            Fn.propAsString(name)(obj)?.toUpperCase()
        }
    }

    static def propAsLowerString = { String name ->
        { def obj ->
            Fn.propAsString(name)(obj)?.toLowerCase()
        }
    }

    static def propAsEncodedString = { String name ->
        { def obj ->
            String result = Fn.propAsString(name)(obj)
            return Fn.encodeUrlString(result)
        }
    }

    static Closure<String> encodeUrlString = { String text ->
        return text == null ? null : URLEncoder.encode(text, 'UTF-8')
    }

    static Closure<String> decodeUrlString = { String text ->
        return text == null ? null : URLDecoder.decode(text, 'UTF-8')
    }

    static def asNumber = { def obj ->
        String stringVal = "$obj".trim()
        if (stringVal.isInteger()) {
            return Integer.valueOf(stringVal)
        } else if (stringVal.isLong()) {
            return Long.valueOf(stringVal)
        } else if (stringVal.isBigInteger()) {
            return new BigInteger(stringVal)
        } else if (stringVal.isBigDecimal()) {
            return new BigDecimal(stringVal)
        } else if (stringVal.isFloat()) {
            return Float.valueOf(stringVal)
        } else if (stringVal.isDouble()) {
            return Double.valueOf(stringVal)
        } else {
            return null
        }
    }

    static def asInteger = { def obj ->
        String stringVal = "$obj".trim()
        return stringVal.isInteger() ? Integer.valueOf(stringVal) : null
    }

    static def asLong = { def obj ->
        String stringVal = "$obj".trim()
        return stringVal.isLong() ? Long.valueOf(stringVal) : null
    }

    static def asDouble = { def obj ->
        Fn.asNumber(obj) as Double
    }

    static def asBigDecimal = { def obj ->
        def v = Fn.asNumber(obj)
        if (v == null) return null
        new BigDecimal(String.valueOf(v))
    }

    static def asBigDecimalWithScale = { Object decimalPlaces, RoundingMode mode ->
        { def obj ->
            Integer decimals = Fn.asInteger(decimalPlaces)
            return decimals != null ? Fn.asBigDecimal(obj)?.setScale(decimals, mode) : Fn.asBigDecimal(obj)
        }
    }

    static def withoutChar = { def obj ->
        if (obj == null) return obj
        return obj.toString().replaceAll(/[a-zA-Z]+/, '')
    }

    static def propAsNumber = { String name ->
        { def obj ->
            Fn.asNumber(Fn.propAsString(name)(obj))
        }
    }

    static def propAsInteger = { String name ->
        { def obj ->
            Fn.asInteger(Fn.propAsString(name)(obj))
        }
    }

    static def asNumberOrLowerCaseString = {
        String stringVal = Fn.asString(it)
        boolean isString = it instanceof String
        boolean isNumberAndNotString = Fn.isNumber(stringVal) && !isString
        if (isNumberAndNotString) return it
        if (StringUtils.isBlank(stringVal)) return stringVal
        return stringVal.toLowerCase()
    }

    static def propAsNumberOrLowerCaseString = { String name ->
        { def obj ->
            Fn.asNumberOrLowerCaseString(Fn.prop(name)(obj ?: [:]))
        }
    }

    static def isBoolean = { def obj ->
        StringUtils.trimToEmpty(Fn.asString(obj)).toLowerCase() in ['true', 'false']
    }

    static def asBoolean = { def obj ->
        obj == null ? obj : Fn.asString(obj).trim()?.toLowerCase() == 'true'
    }

    static def propAsBoolean = { String name ->
        { def obj ->
            return Fn.asBoolean(Fn.propAsString(name)(obj ?: [:]))
        }
    }

    static def propAsLong = { String name ->
        { def obj ->
            return Fn.asLong(Fn.propAsString(name)(obj ?: [:]))
        }
    }

    static Closure<Closure<List>> propAsList = { String name ->
        { def obj ->
            def items = Fn.safeRun({ [] })({ Fn.prop(name)(obj) })
            return items instanceof List ? items : []
        }
    }

    static def isPropIn = { String name, List items ->
        { def obj ->
            if (name == null) return false
            Fn.contains(
                    Fn.propAsString(name)(obj ?: [:])
            )(
                    Fn.mapList(Fn.asString)(items ?: [])
            )
        }
    }

    static def propAsNumberOrLowerCaseStringIn = { String name, List items ->
        { def obj ->
            if (name == null) return false
            return Fn.contains(
                    Fn.propAsNumberOrLowerCaseString(name)(obj ?: [:])
            )(
                    Fn.mapList(Fn.asNumberOrLowerCaseString)(items ?: [])
            )
        }
    }

    //This is changed because it.sum() returns an integer which will overflow with large numbers.
    static def sumBigDecimal = { List<BigDecimal> values ->
        BigDecimal sum = new BigDecimal(0)
        values.each { BigDecimal value ->
            sum = value ? sum.add(value) : sum
        }
        return sum
    }

    static boolean matchesAlphaNumericHyphenUnderscore(String input) {
        input ==~ /^[a-zA-Z0-9-_]+$/
    }

    static def split = { String delimiter ->
        { String input ->
            input.split(delimiter) as List
        }
    }

    static def mapToJsonString = { Map data ->
        JsonOutput.prettyPrint(JsonOutput.toJson(data ?: [:]))
    }

    static def listToJsonString = { List data ->
        JsonOutput.prettyPrint(JsonOutput.toJson(data ?: []))
    }

    static def camelToLowerSnakeCase = { String text ->
        // http://www.groovyconsole.appspot.com/script/337001
        text?.replaceAll(/([A-Z])/, /_$1/)?.toLowerCase()?.replaceAll(/^_/, '')
    }

    static def spacedToLowerSnakeCase = { String text ->
        text?.trim()?.toLowerCase()?.replaceAll(" ", "_")
    }

    static def camelToUpperSnakeCase = { String text ->
        text?.replaceAll(/([A-Z])/, /_$1/)?.toUpperCase()?.replaceAll(/^_/, '')
    }

    static def camelToLowerHyphenCase = { String text ->
        text?.replaceAll(/([A-Z])/, /-$1/)?.toLowerCase()?.replaceAll(/^-/, '')
    }

    static def snakeToHyphenCase = { String text ->
        text?.replaceAll(/([_])/, '-')
    }

    static def hyphenToSnakeCase = { String text ->
        text?.replaceAll(/([-])/, '_')
    }

    static def splitCamelCase = { String s ->
        // https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
        return s?.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        )
    }

    static def splitCamelCaseToLower = { String s ->
        Fn.splitCamelCase(s)?.toLowerCase()
    }

    static def splitCamelCaseToUpper = { String s ->
        Fn.splitCamelCase(s)?.toUpperCase()
    }


    static def hasField = { String fieldName ->
        { Object o ->
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
    }

    static Closure<Boolean> isNull = { Object v ->
        return v == null || (v != 'null' && v.toString() == 'null') // could be org.codehaus.groovy.grails.web.json.JSONObject$Null
    }

    static Closure<Boolean> isNullOrEmptyList = { List list ->
        return !list || list.empty
    }

    static Closure<Class> getFieldType = { String fieldName, Object obj ->
        try {
            Class type = obj.metaClass.getMetaProperty(fieldName).type

            switch (type?.name) {
                case 'int':
                    return Integer.class
                case 'long':
                    return Long.class
                case 'boolean':
                    return Boolean.class
                default:
                    return type
            }
        } catch (Exception ignore) {
            return Object.class
        }
    }

    /**
     * @param v - set the type as def so that primitive types are not auto boxed
     * */
    static def setPrimitiveField = { Object obj, String k, def v ->
        Class type = obj.metaClass.getMetaProperty(k).type
        boolean isNull = Fn.isNull(v) // could be org.codehaus.groovy.grails.web.json.JSONObject$Null

        try {
            switch (type?.name) {
                case 'int':
                    if (!isNull && Fn.isNumber(v)) obj[(k)] = Integer.valueOf(String.valueOf(v))
                    break
                case 'long':
                    if (!isNull && Fn.isNumber(v)) obj[(k)] = Long.valueOf(String.valueOf(v))
                    break
                case 'boolean':
                    if (!isNull && Fn.isBoolean(v)) obj[(k)] = Boolean.valueOf(String.valueOf(v))
                    break
                default:
                    break
            }
            switch (type) {
                case Integer:
                    if (isNull || Fn.isNumber(v)) obj[(k)] = isNull ? null : Integer.valueOf(String.valueOf(v))
                    break
                case Long:
                    if (isNull || Fn.isNumber(v)) obj[(k)] = isNull ? null : Long.valueOf(String.valueOf(v))
                    break
                case BigDecimal:
                    if (isNull || Fn.isNumber(v)) obj[(k)] = isNull ? null : BigDecimal.valueOf(String.valueOf(v))
                    break
                case Double:
                    if (isNull || Fn.isNumber(v)) obj[(k)] = isNull ? null : Double.valueOf(String.valueOf(v))
                    break
                case Float:
                    if (isNull || Fn.isNumber(v)) obj[(k)] = isNull ? null : Float.valueOf(String.valueOf(v))
                    break
                case Boolean:
                    if (isNull || Fn.isBoolean(v)) obj[(k)] = isNull ? null : Boolean.valueOf(String.valueOf(v))
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

    /**
     * Converts a groovy object into a map.
     * */
    static def toMap = { Object o, List<String> customExcludeFields = null ->
        List<String> excludeFields = (customExcludeFields ?: [])
        List<String> keys = Fn.getKeys(o) - excludeFields

        Map m = [:]

        // evaluating props would trigger running all the get methods, which also executes all the customExcludeFields, loop the keys doesn't do so
        keys.each { String key ->
            m[(key)] = o[(key)]
        }

        // id is not in o.properties, so set it here
        boolean excludeIdField = excludeFields.find { it == 'id' }
        if (!excludeIdField && Fn.hasField('id')(o)) {
            m['id'] = o['id']
        }

        return m
    }

    /**
     * Only include primitive and date fields. Exclude object fields. Refer to {@link Fn#NON_CUSTOM_OBJECT_TYPES}.
     * */
    static def toFlatMap = { Object o, List<String> customExcludeFields = null ->
        Map m = Fn.toMap(o, customExcludeFields)

        // exclude custom object (non-primitive) types
        return m.findAll { k, v ->
            if (v == null) {
                o instanceof Map ?
                        ((Map) o).containsKey(k) : // the key of a map can only be a non object, so as long as the key exists, it's a flat type
                        o.hasProperty(k as String)?.type in Fn.NON_CUSTOM_OBJECT_TYPES // need to keep the ones with value is null so that fields with intentional null values would not be lost
            } else {
                v?.class in Fn.NON_CUSTOM_OBJECT_TYPES
            }
        }
    }

    static Closure<List<String>> getKeys = { Object o ->
        boolean isEnum = o?.class?.enum
        if (isEnum) {
            return Fn.getEnumKeys(o.class)
        } else {
            Map props = o instanceof Map ? (o as Map) : (o?.properties ?: [:])
            return (props.keySet() as List<String>) - ['class']
        }
    }

    /**
     * enum.properties throws an error in groovy, so use this instead.
     */
    static Closure<List<String>> getEnumKeys = { Class aClass, List<String> customExcludeFields = null ->
        List<String> excludes = (customExcludeFields ?: []) + ['class', 'declaringClass']
        return (aClass.metaClass.properties*.name - excludes) as List<String>
    }


    static def extractInnerVariables = { String vStartQuote, String vEndQuote ->
        { String text ->
            if (!(text && text.contains(vStartQuote) && text.contains(vEndQuote))) return []

            try {
                String rest = text.substring(text.indexOf(vStartQuote) + vStartQuote.length()) // chop the text and use the rest to avoid endQuote before startQuote
                int startIndex = 0
                int endIndex = rest.indexOf(vEndQuote)
                int nextStartIndex = rest.indexOf(vStartQuote, startIndex)

                while (startIndex >= 0 && (nextStartIndex >= 0 && nextStartIndex < endIndex)) {
                    rest = rest.substring(nextStartIndex + vStartQuote.length())
                    endIndex = rest.indexOf(vEndQuote)
                    nextStartIndex = rest.indexOf(vStartQuote, startIndex)
                }

                String v = rest.substring(startIndex, endIndex)
                rest = rest.substring(endIndex + 1)

                return [(vStartQuote + v + vEndQuote)] + Fn.extractInnerVariables(vStartQuote, vEndQuote)(rest)
            } catch (Exception ignore) {
                return []
            }
        }
    }

    static def safeGet = { def defaultValue ->
        return { Closure fn ->
            try {
                fn()
            } catch (Exception ignore) {
                return defaultValue
            }
        }
    }

    static def sortKeys = { Map map ->
        return map.keySet().sort().collectEntries {
            [(it): (map[(it)])]
        }
    }

    static String getIn(List<String> steps, Map map) {
        try {
            return steps.inject(map, { obj, prop -> obj[prop] })
        } catch (Exception ignore) {
            return null // bad definition of the steps or the map is null
        }
    }

    static def safeRun = { Closure fn ->
        try {
            fn()
        } catch (Exception ignore) {
            // ignore error
        }
    }
}