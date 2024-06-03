package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmValidate

/**
 * @since 08/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmConditionalValidate {

    Closure<Boolean> conditionIsMetFn

    OrmErrorCollector then(OrmErrorCollector collector, String field, List<OrmConstraint> constraints) {
        if (!conditionIsMetFn(collector.domain)) return collector
        return OrmValidate.with(collector, field, constraints)
    }
}
