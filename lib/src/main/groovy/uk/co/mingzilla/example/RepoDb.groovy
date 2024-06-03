package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.conn.ConnectionDetail
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
        ConnectionDetail detail = ConnectionDetail.create([
                "driverClassName": "org.mariadb.jdbc.Driver",
                "url"            : "jdbc:mariadb://localhost:3316/storage",
                "user"           : "root",
                "password"       : "test1234",
        ])
        return ConnectionUtil.getConnection(detail.driverClassName, detail.url, detail.connProperties)
    }
}
