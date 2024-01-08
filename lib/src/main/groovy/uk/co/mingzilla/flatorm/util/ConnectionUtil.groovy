package uk.co.mingzilla.flatorm.util

import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.Driver

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class ConnectionUtil {

    static Connection getConnection(String driverClassName, String url, Properties connectionProperties) {
        try {
            return ((Driver) Class.forName(driverClassName).newInstance()).connect(url, connectionProperties)
        } catch (Exception ex) {
            throw new RuntimeException(ex.message, ex)
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
