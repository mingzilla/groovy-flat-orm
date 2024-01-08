package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.OrmRead

import java.sql.Connection

/**
 * (C) Copyright panintelligence Ltd
 *
 * @since 07/01/2024
 * @author ming.huang
 */
class Db1ActorSpec extends Specification {

    void "Test run"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person

        Db1Actor.run { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listStartWith(connection, 'ADM')
            person = MyPerson.getById(connection, 1)
        }

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
    }

    void "Test runInTx"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person

        Db1Actor.runInTx { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listStartWith(connection, 'ADM')
            person = MyPerson.getById(connection, 1)
        }

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
    }
}
