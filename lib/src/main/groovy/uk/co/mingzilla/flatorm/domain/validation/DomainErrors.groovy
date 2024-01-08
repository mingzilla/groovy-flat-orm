package uk.co.mingzilla.flatorm.domain.validation

import uk.co.mingzilla.flatorm.util.Fn

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
        return !hasErrors()
    }

    boolean hasErrors() {
        List<String> fields = Fn.getKeys(this)
        String invalidField = fields.find {
            Map field = this[(it)] as Map
            return !field.isEmpty()
        }
        return invalidField != null
    }

    Map<String, Map<String, Object>> errors() {
        List<String> fields = Fn.getKeys(this)
        List<String> invalidFields = fields.findAll {
            Map field = this[(it)] as Map
            return !field.isEmpty()
        }
        return invalidFields.collectEntries {
            [(it): (this[(it)])]
        }
    }
}
