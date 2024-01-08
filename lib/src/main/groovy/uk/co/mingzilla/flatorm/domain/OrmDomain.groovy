package uk.co.mingzilla.flatorm.domain

import uk.co.mingzilla.flatorm.domain.validation.DomainErrors

/**
 * @since 02/01/2024
 * @author ming.huang
 */
interface OrmDomain {

    List<OrmMapping> resolveMappings()

    DomainErrors validate()

    String resolveTableName()
}