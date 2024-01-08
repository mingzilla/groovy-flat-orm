package uk.co.mingzilla.flatorm.domain.validation

import uk.co.mingzilla.flatorm.domain.OrmValidate

/**
 * @since 08/01/2024
 * @author ming.huang
 */
class OrmConditionalValidate {

    Closure<Boolean> conditionFn

    DomainAndErrors required(DomainAndErrors domainAndErrors, List<String> fields) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.required(domainAndErrors, fields)
    }

    DomainAndErrors minLength(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.minLength(domainAndErrors, fields, min)
    }

    DomainAndErrors minValue(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.minValue(domainAndErrors, fields, min)
    }

    DomainAndErrors maxValue(DomainAndErrors domainAndErrors, List<String> fields, Long max) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.maxValue(domainAndErrors, fields, max)
    }

    DomainAndErrors inList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.inList(domainAndErrors, fields, values)
    }

    DomainAndErrors notInList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.notInList(domainAndErrors, fields, values)
    }

    DomainAndErrors unique(DomainAndErrors domainAndErrors, List<String> fields, Map<String, List> domainsGroupByLowerCaseKey) {
        if (!conditionFn(domainAndErrors.domain)) domainAndErrors
        return OrmValidate.unique(domainAndErrors, fields, domainsGroupByLowerCaseKey)
    }
}
