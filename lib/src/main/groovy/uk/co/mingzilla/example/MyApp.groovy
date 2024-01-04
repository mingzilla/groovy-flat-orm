package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmRead

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class MyApp {

    static void main(String[] args) {
        Db1Actor.run { Connection connection ->
            List<MyPerson> people1 = OrmRead.listAll(connection, MyPerson.class)
            List<MyPerson> people2 = MyPerson.listWithPrefix(connection, 'Andy')
            MyPerson person = MyPerson.getById(connection, 5)
        }
    }
}
