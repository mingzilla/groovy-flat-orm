package uk.co.mingzilla.flatorm.domain


import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

/**
 * @since 02/01/2024
 * @author ming.huang
 */
interface OrmDomain {

    List<OrmMapping> resolveMappings()

    OrmErrorCollector validate()

    String resolveTableName()
}