package uk.co.mingzilla.flatorm.domain.validation

/**
 * @since 14/01/2024
 * @author ming.huang
 */
enum OrmConstraintType {

    REQUIRED('REQUIRED'),
    MINIMUM_LENGTH('MINIMUM_LENGTH'),
    MINIMUM_VALUE('MINIMUM_VALUE'), // Have error calling it MIN_VALUE, so call it MINIMUM_VALUE
    MAXIMUM_VALUE('MAXIMUM_VALUE'),
    IN_LIST('IN_LIST'),
    NOT_IN_LIST('NOT_IN_LIST'),
    UNIQUE('UNIQUE')

    String value

    OrmConstraintType(String value) {
        this.value = value
    }
}