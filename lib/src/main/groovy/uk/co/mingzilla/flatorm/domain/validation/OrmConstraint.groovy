package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.util.Fn

/**
 * Does not include UNIQUE constraint because it can be faster by running a SQL check.
 *
 * @since 14/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmConstraint {

    OrmConstraintType type // e.g., minValue
    String value // (optional) e.g., when minValue is 5, then 'type' is MINIMUM_VALUE, 'value' is set to 5
    List values // (optional) e.g., when inList is [1,2,3], then 'type' is IN_LIST, 'values' are [1,2,3]

    static OrmConstraint required() {
        return new OrmConstraint(type: OrmConstraintType.REQUIRED)
    }

    static OrmConstraint minLength(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MINIMUM_LENGTH, value: String.valueOf(value))
    }

    static OrmConstraint minValue(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MINIMUM_VALUE, value: String.valueOf(value))
    }

    static OrmConstraint maxValue(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MAXIMUM_VALUE, value: String.valueOf(value))
    }

    static OrmConstraint inList(List values) {
        return new OrmConstraint(type: OrmConstraintType.IN_LIST, values: values)
    }

    static OrmConstraint notInList(List values) {
        return new OrmConstraint(type: OrmConstraintType.NOT_IN_LIST, values: values)
    }

    static boolean isValid(OrmConstraint constraint, Object v) {
        switch (constraint.type) {
            case OrmConstraintType.REQUIRED:
                return StringUtils.isNotBlank(v as String)
            case OrmConstraintType.MINIMUM_LENGTH:
                return v == null || (String.valueOf(v ?: '').size() >= (constraint.value as Integer))
            case OrmConstraintType.MINIMUM_VALUE:
                return v == null || (Fn.isNumber(v) && Fn.asLong(v) >= (constraint.value as Integer))
            case OrmConstraintType.MAXIMUM_VALUE:
                return v == null || (Fn.isNumber(v) && Fn.asLong(v) <= (constraint.value as Integer))
            case OrmConstraintType.IN_LIST:
                return v == null || (v in constraint.values)
            case OrmConstraintType.NOT_IN_LIST:
                return v == null || (!(v in constraint.values))
            default:
                return true
        }
    }
}
