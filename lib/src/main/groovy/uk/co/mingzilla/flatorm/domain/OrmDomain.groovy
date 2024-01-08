package uk.co.mingzilla.flatorm.domain

import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors

/**
 * @since 02/01/2024
 * @author ming.huang
 */
interface OrmDomain {

    List<OrmMapping> resolveMappings()

    DomainAndErrors validate()

    String resolveTableName()
}