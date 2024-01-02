package uk.co.mh.example

import uk.co.mh.flatorm.domain.OrmActor
import uk.co.mh.flatorm.util.ConnectionUtil

import java.sql.Connection

/**
 * Wrapper of {@link OrmActor} targeting a specific database with jndi connection.
 *
 * @since 01/01/2024
 * @author ming.huang
 */
class Db1Actor {

    static void run(Closure fn) {
        Connection connection = ConnectionUtil.getConnection('java:comp/env/jdbc/YourDataSource')
        OrmActor.run(connection, fn)
    }
}
