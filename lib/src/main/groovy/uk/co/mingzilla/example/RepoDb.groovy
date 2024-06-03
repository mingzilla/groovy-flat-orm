package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.util.ConnectionUtil

import java.sql.Connection

/**
 * A specific database.
 *
 * @since 01/01/2024
 * @author ming.huang
 */
class RepoDb {

    static Connection getConn() {
        try {
            return createTargetDbConnection()
        } catch (Exception ex) {
            // Log error here. OrmActor expects a connection.
            throw new RuntimeException(ex.message, ex)
        }
    }

    private static Connection createTargetDbConnection() {
        String driverClassName = 'org.mariadb.jdbc.Driver'
        String url = 'jdbc:mariadb://localhost:9910/dashboard'
        Properties properties = new Properties()
        properties.setProperty('user', 'root')
        properties.setProperty('password', 'BfGWi8T0uSVBkc5OZQ3DhhdR')
        return ConnectionUtil.getConnection(driverClassName, url, properties)
    }
}
