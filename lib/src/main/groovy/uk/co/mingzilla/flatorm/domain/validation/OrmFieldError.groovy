package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic

/**
 * @author ming.huang
 * @since 13/01/2024
 */
@CompileStatic
class OrmFieldError {

    OrmConstraint constraint // e.g., minValue 5
    String field // e.g., age
    Object invalidValue // e.g., 4

    static OrmFieldError create(OrmConstraint constraint, String field, Object invalidValue) {
        OrmFieldError item = new OrmFieldError()
        item.constraint = constraint
        item.field = field
        item.invalidValue = invalidValue
        return item
    }

    Map<String, Object> toMap() {
        Map<String, Object> m = [constraint: constraint.type.value] as Map<String, Object>
        if (constraint.value != null) m['constraintValue'] = constraint.value
        if (constraint.values != null && !constraint.values.empty) m['constraintValues'] = constraint.values.join(', ')
        m['field'] = field
        m['invalidValue'] = invalidValue
        return m
    }
}
