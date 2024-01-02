package uk.co.mh.flatorm.domain

/**
 * @since 02/01/2024
 * @author ming.huang
 */
interface OrmDomain {

    List<OrmMapping> resolveMappings()

    String resolveTableName()
}