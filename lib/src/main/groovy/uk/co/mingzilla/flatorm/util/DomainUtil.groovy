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
     */
    static <T> T mergeFields(T obj, Map<String, Object> newProps) {
        Map<String, Object> relevantProps = (newProps ?: [:]).findAll { String k, def v -> obj.hasProperty(k) }
        relevantProps.each { String k, def v ->
            Fn.setPrimitiveField(obj, k, v)
        }
        return obj
    }
}
