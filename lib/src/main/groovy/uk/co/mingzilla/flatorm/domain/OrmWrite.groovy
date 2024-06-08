package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.IdGen
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

@CompileStatic
class OrmWrite {

    static OrmErrorCollector save(Connection connection, OrmDomain domain) {
        OrmErrorCollector errorCollector = domain.validate()
        if (!errorCollector.hasErrors()) {
            insertOrUpdate(connection, domain)
        }
        return errorCollector
    }

    static OrmDomain insertOrUpdate(Connection conn, OrmDomain domain) {
        boolean isNew = IdGen.isGeneratedId(domain.id)
        if (isNew) {
            PreparedStatement insertStmt = createInsertPreparedStatement(conn, domain)
            int affectedRows = insertStmt.executeUpdate()
            if (affectedRows > 0) {
                OrmMapping idMapping = OrmMapping.getIdMapping(domain.resolveMappings())
                domain.id = resolveId(insertStmt.generatedKeys, idMapping)
            }
        } else {
            PreparedStatement updateStmt = createUpdatePreparedStatement(conn, domain)
            updateStmt.executeUpdate()
        }
        return domain
    }

    private static Integer resolveId(ResultSet generatedKeys, OrmMapping idMapping) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        ResultSetMetaData metaData = generatedKeys.metaData
        int columnCount = metaData.columnCount
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i)
            if (idMapping.dbFieldName.equalsIgnoreCase(columnName)) {
                return generatedKeys.getInt(i)
            }
        }
        return null
    }

    static PreparedStatement createInsertPreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String insertSql = createInsertStatement(domain.tableName(), nonIdMappings)
        PreparedStatement insertStmt = conn.prepareStatement(insertSql)
        insertStmt = setStatementParams(insertStmt, domain, nonIdMappings)
        return insertStmt
    }

    static String createInsertStatement(String tableName, List<OrmMapping> nonIdMappings) {
        String fieldNames = nonIdMappings*.dbFieldName.join(', ')
        String valuePlaceholders = nonIdMappings.collect { '?' }.join(', ')
        String statement = """insert into ${tableName.toLowerCase()} (${fieldNames}) values (${valuePlaceholders})"""
        return statement
    }

    static PreparedStatement createUpdatePreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        OrmMapping idMapping = idAndNonIdMappings[0][0]
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String insertSql = createUpdateStatement(domain.tableName(), domain.id, idMapping, nonIdMappings)
        PreparedStatement insertStmt = conn.prepareStatement(insertSql)
        insertStmt = setStatementParams(insertStmt, domain, nonIdMappings)
        return insertStmt
    }

    static String createUpdateStatement(String tableName, Integer id, OrmMapping idMapping, List<OrmMapping> nonIdMappings) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        String setStatement = nonIdMappings.collect { "${it.dbFieldName} = ?" }.join(', ')
        String statement = """update ${tableName.toLowerCase()} set ${setStatement} where ${idMapping.dbFieldName} = ${String.valueOf(id)}"""
        return statement
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
}
