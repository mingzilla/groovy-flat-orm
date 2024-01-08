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

    static void run(Connection connection, Closure fn) {
        if (!connection) return
        try {
            fn(connection)
        } finally {
            ConnectionUtil.close(connection)
        }
    }

    /**
     * Run in a transaction.
     */
    static void runInTx(Connection connection, Closure fn) {
        if (!connection) return
        try {
            connection.setAutoCommit(false)
            fn(connection)
            connection.commit()
        } catch (Exception ignore) {
            connection.rollback()
        } finally {
            ConnectionUtil.close(connection)
        }
    }
}
