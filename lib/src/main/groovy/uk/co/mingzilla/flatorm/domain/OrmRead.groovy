package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
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

    static Closure<PreparedStatement> NO_PARAMS = { PreparedStatement it -> it }

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     */
    static <T> List<T> listAll(Connection conn, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String selectStatement = "select * from ${domain.tableName()}"
        return listAndMerge(conn, mappings, selectStatement, NO_PARAMS,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * Similar to {@link #listAll}. Intended to be used with a custom WHERE clause.
     */
    static <T> List<T> list(Connection conn, Class aClass, String selectStatement, Closure<PreparedStatement> setParamsFn) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return listAndMerge(conn, mappings, selectStatement, setParamsFn,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     */
    static <T> List<T> listAndMerge(Connection conn, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<PreparedStatement> setParamsFn, Closure<T> createDomainFn) {
        List<T> objs = []
        PreparedStatement statement
        ResultSet resultSet

        try {
            statement = conn.prepareStatement(selectStatement.toString())
            statement = setParamsFn(statement)
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
    static <T> T getById(Connection conn, Class aClass, def id) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String idField = mappings.find { it.camelFieldName == 'id' }?.dbFieldName
        String selectStatement = "SELECT * FROM ${domain.tableName()} WHERE ${idField} = ${id}"
        return getAndMerge(conn, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * When used, the select statement typically needs a WHERE clause.
     */
    static <T> T getFirst(Connection conn, Class aClass, String selectStatement) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return getAndMerge(conn, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * Same as {@link #listAndMerge}, but only return the 1st object found
     */
    static <T> T getAndMerge(Connection conn, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<T> createDomainFn) {
        try {
            PreparedStatement statement = conn.prepareStatement(selectStatement.toString())
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
    static Long count(Connection conn, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        String selectStatement = "select count(*) from ${domain.tableName()}".toString()
        return getCount(conn, selectStatement)
    }

    /**
     * Intended to be used for a SELECT count(*) statement, which also allows e.g. JOIN and WHERE clause.
     */
    private static Long getCount(Connection conn, String selectStatement) {
        PreparedStatement statement
        ResultSet resultSet

        Long count = 0
        try {
            statement = conn.prepareStatement(selectStatement)
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
