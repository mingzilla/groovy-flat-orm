package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.OrmRead

import java.sql.Connection

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class Db1ActorSpec extends Specification {

    void "Test run"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person
        long count = 0

        Db1Actor.run { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listByNameStartsWith(connection, 'ADM') // custom sql
            person = OrmRead.getById(connection, MyPerson.class, 1)
            count = OrmRead.count(connection, MyPerson.class)
        }

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
        count > 0
    }

    void "Test runInTx"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person

        Db1Actor.runInTx { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listByNameStartsWith(connection, 'ADM') // custom sql
            person = OrmRead.getById(connection, MyPerson.class, 1)
        }

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
    }
}
