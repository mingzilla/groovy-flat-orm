package uk.co.mingzilla.flatorm.domain

import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors
import uk.co.mingzilla.flatorm.util.Fn

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class OrmValidate {

    static DomainAndErrors required(DomainAndErrors domainAndErrors, List<String> fields) {
        return validate(domainAndErrors, 'required', fields, { StringUtils.isBlank(String.valueOf(it)) })
    }

    static DomainAndErrors minLength(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        return validate(domainAndErrors, 'minLength', fields, { String.valueOf(it ?: '').size() >= min })
    }

    static DomainAndErrors minValue(DomainAndErrors domainAndErrors, List<String> fields, Long min) {
        return validate(domainAndErrors, 'minValue', fields, { Fn.isNumber(it) && Fn.asNumber(it) >= min })
    }

    static DomainAndErrors maxValue(DomainAndErrors domainAndErrors, List<String> fields, Long max) {
        return validate(domainAndErrors, 'maxValue', fields, { Fn.isNumber(it) && Fn.asNumber(it) <= max })
    }

    static DomainAndErrors inList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        return validate(domainAndErrors, 'inList', fields, { it in values })
    }

    static DomainAndErrors notInList(DomainAndErrors domainAndErrors, List<String> fields, List values) {
        return validate(domainAndErrors, 'notInList', fields, { !(it in values) })
    }

    /**
     * domainsGroupByLowerCaseKey: key is in lower case, value is a list of items, and items are all the objects, including the current object to validate
     */
    static DomainAndErrors unique(DomainAndErrors domainAndErrors, List<String> fields, Map<String, List> domainsGroupByLowerCaseKey) {
        return validate(domainAndErrors, 'unique', fields, {
            String key = (it ?: '').toLowerCase()
            List items = domainsGroupByLowerCaseKey.get(key) ?: []
            return items.length > 1
        })
    }

    static DomainAndErrors validate(DomainAndErrors domainAndErrors, String errorType, List<String> fields, Closure<Boolean> ifTrueThenFailValidationFn) {
        Map<String, Object> fieldAndValue = fields.collectEntries {
            Object fieldValue = domainAndErrors.domain[(it)]
            return [(it): fieldValue]
        }
        Map<String, Object> invalidFieldAndValue = fieldAndValue.findAll { ifTrueThenFailValidationFn(it.value) }
        return domainAndErrors.mergeErrors(errorType, invalidFieldAndValue)
    }

    static Map<String, Closure<DomainAndErrors>> whenSatisfies(DomainAndErrors domainAndErrors, Closure<Boolean> conditionFn) {
        return [
                required : { List<String> fields ->
                    return conditionFn(domainAndErrors.domain) ?
                            required(domainAndErrors, fields)(domainAndErrors) :
                            domainAndErrors
                },
                minLength: { List<String> fields, Long min ->
                    return conditionFn(domainAndErrors.domain) ?
                            minLength(domainAndErrors, fields, min)(domainAndErrors) :
                            domainAndErrors
                },
                minValue : { List<String> fields, Long min ->
                    return conditionFn(domainAndErrors.domain) ?
                            minValue(domainAndErrors, fields, min)(domainAndErrors) :
                            domainAndErrors
                },
                maxValue : { List<String> fields, Long max ->
                    return conditionFn(domainAndErrors.domain) ?
                            maxValue(domainAndErrors, fields, max)(domainAndErrors) :
                            domainAndErrors
                },
                inList   : { List<String> fields, List values ->
                    return conditionFn(domainAndErrors.domain) ?
                            inList(domainAndErrors, fields, values)(domainAndErrors) :
                            domainAndErrors
                },
                notInList: { List<String> fields, List values ->
                    return conditionFn(domainAndErrors.domain) ?
                            notInList(domainAndErrors, fields, values)(domainAndErrors) :
                            domainAndErrors
                },
                unique   : { List<String> fields, Map<String, List> domainsGroupByLowerCaseKey ->
                    return conditionFn(domainAndErrors.domain) ?
                            unique(domainAndErrors, fields, domainsGroupByLowerCaseKey)(domainAndErrors) :
                            domainAndErrors
                },
        ]
    }
}
