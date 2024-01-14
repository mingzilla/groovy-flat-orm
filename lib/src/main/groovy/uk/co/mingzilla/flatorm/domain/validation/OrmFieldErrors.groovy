package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic

/**
 * @since 14/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmFieldErrors {

    String field // e.g., age
    List<OrmFieldError> errors = []

    static OrmFieldErrors create(String field) {
        return new OrmFieldErrors(field: field)
    }

    OrmFieldErrors addError(OrmFieldError fieldError) {
        errors.add(fieldError)
        return this
    }

    boolean hasErrors() {
        return !errors.empty
    }
}
