package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.domain.validation.OrmConditionalValidate
import uk.co.mingzilla.flatorm.domain.validation.OrmConstraint
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.domain.validation.OrmFieldError
import uk.co.mingzilla.flatorm.util.Fn

/**
 * @since 14/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmValidate {

    static OrmErrorCollector with(OrmErrorCollector collector, String field, List<OrmConstraint> constraints) {
        Object value = collector.domain[(field)]
        constraints.each {
            collectError(collector, it, field, value)
        }
        return collector
    }

    private static OrmErrorCollector collectError(OrmErrorCollector collector, OrmConstraint constraint, String field, Object value) {
        if (OrmConstraint.isValid(constraint, value)) return collector

        OrmFieldError fieldError = OrmFieldError.create(constraint, field, value)
        collector.addError(fieldError)
        return collector
    }

    static OrmConditionalValidate ifHaving(String field) {
        Closure<Boolean> conditionIsMetFn = { OrmDomain it ->
            String v = Fn.propAsString(field)(it)
            return StringUtils.isNotBlank(v)
        }
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }

    static OrmConditionalValidate ifNotHaving(String field) {
        Closure<Boolean> conditionIsMetFn = { OrmDomain it ->
            String v = Fn.propAsString(field)(it)
            return StringUtils.isBlank(v)
        }
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }

    static OrmConditionalValidate ifSatisfies(Closure<Boolean> conditionIsMetFn) {
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }
}
