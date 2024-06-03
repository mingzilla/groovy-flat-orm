package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

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
        int idToCheck = 5
        String nameToUpdate = "John"

        List<OrmMapping> mappings = domain.resolveMappings()
        OrmMapping idMapping = mappings.find { it.camelFieldName?.equalsIgnoreCase('id') }

        String selectSql = "SELECT COUNT(*) FROM ${domain.resolveTableName()} WHERE id = ?"
        String insertSql = "INSERT INTO users (id, name) VALUES (?, ?)"
        String updateSql = "UPDATE users SET name = ? WHERE id = ?"

        PreparedStatement selectStmt = conn.prepareStatement(selectSql)
        PreparedStatement insertStmt = conn.prepareStatement(insertSql)
        PreparedStatement updateStmt = conn.prepareStatement(updateSql)

        // Check if the record exists
        selectStmt.setInt(1, idToCheck)
        ResultSet rs = selectStmt.executeQuery()
        rs.next()
        int count = rs.getInt(1)

        int newId
        if (count > 0) {
            // Update if the record exists
            updateStmt.setString(1, nameToUpdate)
            updateStmt.setInt(2, idToCheck)
            updateStmt.executeUpdate()
            newId = idToCheck
        } else {
            // Insert if the record doesn't exist
            insertStmt.setString(1, nameToUpdate)
            int affectedRows = insertStmt.executeUpdate()
            if (affectedRows > 0) {
                ResultSet generatedKeys = insertStmt.getGeneratedKeys()
                if (generatedKeys.next()) {
                    newId = generatedKeys.getInt(1)
                }
            }
        }

        return null // todo - should return the same item with db created id when performing an insert
    }
}
