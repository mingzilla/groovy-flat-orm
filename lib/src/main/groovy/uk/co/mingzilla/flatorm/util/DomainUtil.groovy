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

    /**
     * Merge data submitted from the client side to the server side, which allows submitting only a single field to update one field using the API, without having to submit every single field.
     *
     * Used by Domain.mergeData(), so that setting data is consistently handled.
     * This develops a consistent procedure, so that devs don't need to always consider if they need to use
     * <pre>
     * - e.g.`this.myField = myField` - set the value without fallback
     * - or`this.myField = myField ?: this.myField` - picks up the db value if value supplied is null
     * </pre>
     *
     * @param resolvedProps - values resolved on the server side
     * @param unmodifiedClientSubmittedProps - used to find out if user submits a value, if they do not submit null, and server side resolves null, use db value because user has no intent to clear it
     *
     * Scenarios:
     * <pre>
     * - if user intentionally sets value x, and resolved as x, use x
     * - if user intentionally sets value x, and resolved as y, use y
     * - if user intentionally sets value null, and resolved as y, use y
     * - if user intentionally sets value null, and resolved as null, use null
     * - if user does not submit field (no intent to change), and resolved as null (because a variable is created to process the logic), use db value
     *   - mostly occurs when using the API to update without supplying every single field
     * </pre>
     */
    static <T> T mergeRequestData(T obj, Map<String, Object> resolvedProps, Map<String, Object> unmodifiedClientSubmittedProps) {
        Map<String, Object> newProps = resolvedProps.collectEntries { String k, Object v ->
            boolean clientSendsKey = unmodifiedClientSubmittedProps.containsKey(k)
            boolean clientSetsNull = unmodifiedClientSubmittedProps[(k)] == null
            boolean serverSetsValue = v != null

            boolean hasFieldAndSetToNull = clientSendsKey && clientSetsNull
            if (serverSetsValue) {
                return [(k): (v)]
            } else if (hasFieldAndSetToNull) {
                [(k): null]
            } else {
                Object dbValue = obj[(k)]
                [(k): (dbValue)]
            }
        }
        return mergeFields(obj, newProps)
    }
}
