package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.util.ConnectionUtil

import java.sql.Connection

/**
 * Wrapper of {@link OrmActor} targeting a specific database with jndi connection.
 *
 * @since 01/01/2024
 * @author ming.huang
 */
class Db1Actor {

    static void run(Closure fn) {
        OrmActor.run(createConnection(), fn)
    }

    static void runInTx(Closure fn) {
        OrmActor.runInTx(createConnection(), fn)
    }

    private static Connection createConnection() {
        return ConnectionUtil.getConnection('java:comp/env/jdbc/YourDataSource')
    }
}
