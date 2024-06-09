package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.IdGen
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.*
import java.util.Date

@CompileStatic
class OrmWrite {

    static OrmErrorCollector save(Connection conn, OrmDomain domain) {
        OrmErrorCollector errorCollector = domain.validate()
        if (!errorCollector.hasErrors()) {
            insertOrUpdate(conn, domain)
        }
        return errorCollector
    }

    static boolean delete(Connection conn, OrmDomain domain) {
        PreparedStatement statement = createDeletePreparedStatement(conn, domain)
        int rowsAffected = statement.executeUpdate()
        return rowsAffected > 0 // return true if row is deleted
    }

    static OrmDomain insertOrUpdate(Connection conn, OrmDomain domain) {
        boolean isNew = IdGen.isGeneratedId(domain.id)
        if (isNew) {
            PreparedStatement statement = createInsertPreparedStatement(conn, domain)
            int rowsAffected = statement.executeUpdate()
            if (rowsAffected > 0) {
                OrmMapping idMapping = OrmMapping.getIdMapping(domain.resolveMappings())
                domain.id = resolveId(statement.generatedKeys, idMapping)
            }
        } else {
            PreparedStatement updateStmt = createUpdatePreparedStatement(conn, domain)
            updateStmt.executeUpdate()
        }
        return domain
    }

    static PreparedStatement createInsertPreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String sql = createInsertStatement(domain.tableName(), nonIdMappings)
        PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        statement = setStatementParams(statement, domain, nonIdMappings)
        return statement
    }

    static String createInsertStatement(String tableName, List<OrmMapping> nonIdMappings) {
        String fieldNames = nonIdMappings*.dbFieldName.join(', ')
        String valuePlaceholders = nonIdMappings.collect { '?' }.join(', ')
        return """insert into ${tableName.toLowerCase()} (${fieldNames}) values (${valuePlaceholders})"""
    }

    static PreparedStatement createUpdatePreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        OrmMapping idMapping = idAndNonIdMappings[0][0]
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String sql = createUpdateStatement(domain.tableName(), domain.id, idMapping, nonIdMappings)
        PreparedStatement statement = conn.prepareStatement(sql)
        statement = setStatementParams(statement, domain, nonIdMappings)
        return statement
    }

    static String createUpdateStatement(String tableName, Integer id, OrmMapping idMapping, List<OrmMapping> nonIdMappings) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        String setStatement = nonIdMappings.collect { "${it.dbFieldName} = ?" }.join(', ')
        return """update ${tableName.toLowerCase()} set ${setStatement} where ${idMapping.dbFieldName} = ${String.valueOf(id)}"""
    }

    static PreparedStatement createDeletePreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        OrmMapping idMapping = idAndNonIdMappings[0][0]
        String sql = createDeleteStatement(domain.tableName(), idMapping)
        PreparedStatement statement = conn.prepareStatement(sql)
        statement.setInt(1, domain.id);
        return statement
    }

    static String createDeleteStatement(String tableName, OrmMapping idMapping) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        return """delete from ${tableName.toLowerCase()} where ${idMapping.dbFieldName} = ?"""
    }

    static PreparedStatement setStatementParams(PreparedStatement statement, OrmDomain domain, List<OrmMapping> nonIdMappings) {
        nonIdMappings.eachWithIndex { OrmMapping it, Integer index ->
            Integer oneBasedPosition = index + 1
            Class type = InFn.getType(domain.class, it.camelFieldName)
            switch (type) {
                case boolean:
                case Boolean.class:
                    Boolean v = InFn.propAsBoolean(it.camelFieldName, domain)
                    statement.setBoolean(oneBasedPosition, v)
                    break
                case BigDecimal.class:
                    BigDecimal v = InFn.propAsBigDecimal(it.camelFieldName, domain)
                    statement.setBigDecimal(oneBasedPosition, v)
                    break
                case Date.class:
                    try {
                        Date d = InFn.prop(it.camelFieldName, domain) as Date
                        statement.setDate(oneBasedPosition, new java.sql.Date(d.time))
                    } catch (Exception ignore) {
                        // ignore invalid date
                    }
                    break
                case double:
                case Double.class:
                    Double v = InFn.propAsDouble(it.camelFieldName, domain)
                    statement.setDouble(oneBasedPosition, v)
                    break
                case float:
                case Float.class:
                    Float v = InFn.propAsFloat(it.camelFieldName, domain)
                    statement.setFloat(oneBasedPosition, v)
                    break
                case int:
                case Integer.class:
                    Integer v = InFn.propAsInteger(it.camelFieldName, domain)
                    statement.setInt(oneBasedPosition, v)
                    break
                case long:
                case Long.class:
                    Long v = InFn.propAsLong(it.camelFieldName, domain)
                    statement.setLong(oneBasedPosition, v)
                    break
                case String.class:
                    String v = InFn.propAsString(it.camelFieldName, domain)
                    statement.setString(oneBasedPosition, v)
                    break
                default:
                    break
            }
        }

        return statement
    }

    private static Integer resolveId(ResultSet generatedKeys, OrmMapping idMapping) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        if (!generatedKeys.next()) return null // call next() to move the ResultSet cursor
        ResultSetMetaData metaData = generatedKeys.metaData
        int columnCount = metaData.columnCount
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i)
            if (idMapping.dbFieldName.equalsIgnoreCase(columnName)) {
                return generatedKeys.getInt(i)
            }
        }
        // it is possible that a driver is implemented to return 'insert_id' (rather than using the actual column name) as the columnName
        // If that happens, we fallback to use 1. Typically, the generated key is the first column in the ResultSet
        return generatedKeys.getInt(1)
    }
}
