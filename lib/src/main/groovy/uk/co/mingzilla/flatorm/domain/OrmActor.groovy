package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.util.ConnectionUtil

import java.sql.Connection

/**
 * Used at the out most layer (e.g. a controller, or the main method) to run a request.
 * It safely closes database connection so that other places doesn't need to worry about connection closing.
 *
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmActor {

    static <T> T run(Connection connection, Closure<T> fn) {
        if (!connection) return null
        T result = null
        try {
            result = fn(connection)
        } catch (Exception ignore) {
        } finally {
            ConnectionUtil.close(connection)
        }
        return result
    }

    /**
     * Run in a transaction.
     */
    static <T> T runInTx(Connection connection, Closure<T> fn) {
        if (!connection) return null
        T result = null
        try {
            connection.setAutoCommit(false)
            result = fn(connection)
            connection.commit()
        } catch (Exception ignore) {
            connection.rollback()
        } finally {
            ConnectionUtil.close(connection)
        }
        return result
    }
}
