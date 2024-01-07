package uk.co.mingzilla.flatorm.domain.validation

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class DomainErrors {

    Map<String, Object> required = [:] // fieldName, invalid value
    Map<String, Object> minLength = [:] // fieldName, invalid value
    Map<String, Object> minValue = [:] // fieldName, invalid value
    Map<String, Object> maxValue = [:] // fieldName, invalid value
    Map<String, Object> inList = [:] // fieldName, invalid value
    Map<String, Object> notInList = [:] // fieldName, invalid value
    Map<String, Object> unique = [:] // fieldName, invalid value

    static DomainErrors create() {
        return new DomainErrors()
    }

    boolean hasNoErrors() {
        List<String> fields = Fn.getKeys(this)
        String invalidField = fields.find {
            Map field = this[(it)] as Map
            return !field.isEmpty()
        }
        return invalidField != null
    }

    boolean hasErrors() {
        return !hasNoErrors();
    }
}
