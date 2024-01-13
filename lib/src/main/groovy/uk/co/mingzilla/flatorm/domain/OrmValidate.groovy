package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors
import uk.co.mingzilla.flatorm.domain.validation.OrmConditionalValidate
import uk.co.mingzilla.flatorm.util.Fn

/**
 * @since 07/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmValidate {

    static DomainAndErrors required(DomainAndErrors domainAndErrors, List<String> fields) {
        return validate(domainAndErrors, 'required', fields, { StringUtils.isNotBlank(it as String) })
    }

    static DomainAndErrors minLength(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        return validate(domainAndErrors, 'minLength', fields, { it == null || (String.valueOf(it ?: '').size() >= min) })
    }

    static DomainAndErrors minValue(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        return validate(domainAndErrors, 'minValue', fields, { it == null || (Fn.isNumber(it) && Fn.asLong(it) >= min) })
    }

    static DomainAndErrors maxValue(DomainAndErrors domainAndErrors, List<String> fields, Long max) {
        return validate(domainAndErrors, 'maxValue', fields, { it == null || (Fn.isNumber(it) && Fn.asLong(it) <= max) })
    }

    static DomainAndErrors inList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        return validate(domainAndErrors, 'inList', fields, { it == null || (it in values) })
    }

    static DomainAndErrors notInList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        return validate(domainAndErrors, 'notInList', fields, { it == null || (!(it in values)) })
    }

    /**
     * domainsGroupByLowerCaseKey: key is in lower case, value is a list of items, and items are all the objects, including the current object to validate
     */
    static DomainAndErrors unique(DomainAndErrors domainAndErrors, List<String> fields, Map<String, List> domainsGroupByLowerCaseKey) {
        return validate(domainAndErrors, 'unique', fields, {
            String key = String.valueOf(it ?: '').toLowerCase()
            List items = domainsGroupByLowerCaseKey.get(key) ?: []
            return items.size() > 1
        })
    }

    static DomainAndErrors validate(DomainAndErrors domainAndErrors, String errorType, List<String> fields, Closure<Boolean> isValid) {
        Map<String, Object> fieldAndValue = fields.collectEntries {
            Object fieldValue = domainAndErrors.domain[(it)]
            return [(it): fieldValue]
        }
        Map<String, Object> invalidFieldAndValue = fieldAndValue.findAll { !isValid(it.value) }
        return domainAndErrors.mergeErrors(errorType, invalidFieldAndValue)
    }

    static OrmConditionalValidate ifSatisfies(Closure<Boolean> conditionFn) {
        return new OrmConditionalValidate(conditionFn: conditionFn)
    }

    static OrmConditionalValidate ifHaving(String field) {
        Closure<Boolean> conditionFn = { OrmDomain it ->
            String v = Fn.propAsString(field)(it)
            return StringUtils.isNotBlank(v)
        }
        return new OrmConditionalValidate(conditionFn: conditionFn)
    }

    static OrmConditionalValidate ifNotHaving(String field) {
        Closure<Boolean> conditionFn = { OrmDomain it ->
            String v = Fn.propAsString(field)(it)
            return StringUtils.isBlank(v)
        }
        return new OrmConditionalValidate(conditionFn: conditionFn)
    }
}
