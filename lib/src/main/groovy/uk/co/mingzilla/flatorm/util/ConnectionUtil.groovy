package uk.co.mingzilla.flatorm.util

import javax.naming.InitialContext
import javax.naming.NamingException
import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class ConnectionUtil {

    static Connection getConnection(String jndi) {
        try {
            DataSource dataSource = new InitialContext().lookup(jndi) as DataSource
            return dataSource.connection
        } catch (SQLException | NamingException e) {
            e.printStackTrace()
            throw e
        }
    }

    static void close(Connection connection) {
        try {
            connection?.close()
        } catch (Exception ignore) {
            // do nothing - don't mind if the close fails
        }
    }
}
