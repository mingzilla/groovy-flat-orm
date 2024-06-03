package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class MyApp {

    static void main(String[] args) {
        OrmActor.run(RepoDb.conn, { Connection connection ->
            List<MyPerson> people1 = OrmRead.listAll(connection, MyPerson.class)
            List<MyPerson> people2 = MyPerson.listByNameStartsWith(connection, 'Andy')
            MyPerson person = OrmRead.getById(connection, MyPerson.class, 1)
        })
    }
}
