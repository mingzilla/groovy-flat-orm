package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.util.DomainUtil
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmRead {

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     */
    static <T> List<T> listAll(Connection connection, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String selectStatement = "SELECT * FROM ${domain.resolveTableName()}"
        return listAndMerge(connection, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    static <T> List<T> list(Connection connection, Class aClass, String selectStatement) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return listAndMerge(connection, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     * todo - use params for preparedStatements
     */
    static <T> List<T> listAndMerge(Connection connection, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<T> createDomainFn) {
        List<T> objs = []
        PreparedStatement statement
        ResultSet resultSet

        try {
            statement = connection.prepareStatement(selectStatement.toString())
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                T domain = OrmMapping.toDomain(dbDomainFieldMappings, resultSet, createDomainFn)
                objs.add(domain)
            }
        } catch (SQLException sqlEx) {
            RuntimeException ex = new RuntimeException("Failed running select statement to create object: $sqlEx.message", sqlEx)
            throw ex
        }
        return objs
    }

    /**
     * When used, the select statement typically needs a WHERE clause.
     */
    static <T> T getById(Connection connection, Class aClass, def id) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String idField = mappings.find { it.camelFieldName == 'id' }?.dbFieldName
        String selectStatement = "SELECT * FROM ${domain.resolveTableName()} WHERE ${idField} = ${id}"
        return getAndMerge(connection, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * When used, the select statement typically needs a WHERE clause.
     */
    static <T> T getFirst(Connection connection, Class aClass, String selectStatement) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return getAndMerge(connection, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * Same as {@link #listAndMerge}, but only return the 1st object found
     */
    static <T> T getAndMerge(Connection connection, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<T> createDomainFn) {
        try {
            PreparedStatement statement = connection.prepareStatement(selectStatement.toString())
            ResultSet resultSet = statement.executeQuery()
            try {
                resultSet.next()
                return OrmMapping.toDomain(dbDomainFieldMappings, resultSet, createDomainFn)
            } catch (Exception ignore) {
                return null // if valid SQL doesn't have data, then return null
            }
        } catch (SQLException sqlEx) {
            RuntimeException ex = new RuntimeException("Failed running select statement to create object: $sqlEx.message", sqlEx)
            throw ex
        }
    }

    /**
     * Count table records with a given table name.
     */
    static Long count(Connection connection, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        String selectStatement = "SELECT count(*) from ${domain.resolveTableName()}".toString()
        return getCount(connection, selectStatement)
    }

    /**
     * Intended to be used for a SELECT count(*) statement, which also allows e.g. JOIN and WHERE clause.
     */
    private static Long getCount(Connection connection, String selectStatement) {
        PreparedStatement statement
        ResultSet resultSet

        Long count = 0
        try {
            statement = connection.prepareStatement(selectStatement)
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                count = InFn.asLong(resultSet.getObject(1))
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed running select statement to count records: " + e.message, e)
        }

        return count
    }
}
