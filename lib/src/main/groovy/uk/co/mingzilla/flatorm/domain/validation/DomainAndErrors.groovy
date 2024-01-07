package uk.co.mingzilla.flatorm.domain.validation

import uk.co.mingzilla.flatorm.domain.OrmDomain

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class DomainAndErrors {

    OrmDomain domain
    DomainErrors domainErrors

    static DomainAndErrors create(OrmDomain domain) {
        return new DomainAndErrors(domain: domain, domainErrors: DomainErrors.create())
    }

    /**
     * @param errorType - a field on {@link DomainErrors} e.g. required, minValue
     * @param invalidFieldAndValue - e.g. [age: -5, height: -10]
     */
    DomainAndErrors mergeErrors(String errorType, Map<String, Object> invalidFieldAndValue) {
        (domainErrors[(errorType)] as Map).putAll(invalidFieldAndValue)
        return this
    }
}
