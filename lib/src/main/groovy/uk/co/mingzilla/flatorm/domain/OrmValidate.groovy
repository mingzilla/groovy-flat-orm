package uk.co.mingzilla.flatorm.domain

import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors
import uk.co.mingzilla.flatorm.util.Fn

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class OrmValidate {

    static Closure<DomainAndErrors> validate(String errorType, List<String> fields, Closure<Boolean> ifTrueThenFailValidationFn) {
        return { DomainAndErrors domainAndErrors ->
            Map<String, Object> fieldAndValue = fields.collectEntries {
                Object fieldValue = domainAndErrors.domain[(it)]
                return [(it): fieldValue]
            }
            Map<String, Object> invalidFieldAndValue = fieldAndValue.findAll { ifTrueThenFailValidationFn(it.value) }
            return domainAndErrors.mergeErrors(errorType, invalidFieldAndValue)
        }
    }

    static Closure<DomainAndErrors> required(List<String> fields) {
        return validate('required', fields, { StringUtils.isBlank(it) })
    }

    static Closure<DomainAndErrors> minLength(List<String> fields, Long min) {
        return validate('minLength', fields, { String.valueOf(it ?: '').size() >= min })
    }

    static Closure<DomainAndErrors> minValue(List<String> fields, Long min) {
        return validate('minValue', fields, { Fn.isNumber(it) && Fn.asNumber(it) >= min })
    }

    static Closure<DomainAndErrors> maxValue(List<String> fields, Long max) {
        return validate('maxValue', fields, { Fn.isNumber(it) && Fn.asNumber(it) <= max })
    }

    static Closure<DomainAndErrors> inList(List<String> fields, List values) {
        return validate('inList', fields, { it in values })
    }

    static Closure<DomainAndErrors> notInList(List<String> fields, List values) {
        return validate('notInList', fields, { !(it in values) })
    }

    /**
     * domainsGroupByLowerCaseKey: key is in lower case, value is a list of items, and items are all the objects, including the current object to validate
     */
    static Closure<DomainAndErrors> unique(List<String> fields, Map<String, List> domainsGroupByLowerCaseKey) {
        return validate('unique', fields, {
            String key = (it ?: '').toLowerCase()
            List items = domainsGroupByLowerCaseKey.get(key) ?: []
            return items.length > 1
        })
    }
}
