package uk.co.mingzilla.flatorm.util

import groovy.transform.CompileStatic

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class DomainUtil {

    /**
     * Basic type includes: int, Integer, boolean, Boolean, String, Date
     * String is trimToEmpty. Domain objects are supposed to be saved to db, A value should not just have empty space or space around.
     * However, this doesn't do trimToNull for compatibility purposes. When working on existing bad code, trimToEmpty wouldn't make it hard for those devs.
     */
    static <T> T mergeFields(T obj, Map<String, Object> newProps) {
        Map<String, Object> relevantProps = (newProps ?: [:]).findAll { String k, def v -> obj.hasProperty(k) }
        relevantProps.each { String k, def v ->
            InFn.setPrimitiveField(obj, k, InFn.trimToEmptyIfIsString(v))
        }
        return obj
    }
}
