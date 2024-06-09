package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain

/**
 * @since 14/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmErrorCollector {

    OrmDomain domain
    Map<String, OrmFieldErrors> fields = [:] // key: name of a field, value: a collection of errors

    static OrmErrorCollector create(OrmDomain domain) {
        return new OrmErrorCollector(domain: domain)
    }

    void addError(OrmFieldError fieldError) {
        String field = fieldError.field
        if (!fields[(field)]) fields[(field)] = OrmFieldErrors.create(field)

        OrmFieldErrors fieldErrors = fields[(field)]
        fieldErrors.addError(fieldError)
    }

    boolean hasErrors() {
        return fields.find { it.value.hasErrors() } != null
    }

    static boolean haveErrors(List<List<OrmErrorCollector>> collectors) {
        OrmErrorCollector itemWithError = collectors.flatten().<OrmErrorCollector> toList().find { it?.hasErrors() }
        return itemWithError != null
    }

    static List<Map<String, List<Map>>> toErrorMaps(List<OrmErrorCollector> collectors) {
        List<OrmErrorCollector> itemWithError = collectors.<OrmErrorCollector> toList().findAll { it?.hasErrors() }
        return itemWithError*.toMap()
    }

    Map<String, List<Map>> toMap() {
        return fields.collectEntries {
            [(it.key): it.value.errors*.toMap()]
        }
    }
}
