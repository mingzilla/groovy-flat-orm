package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class MyApp {

    static void main(String[] args) {
        OrmActor.run(RepoDb.conn, { Connection conn ->
            List<MyPerson> people1 = OrmRead.listAll(conn, MyPerson.class)
            List<MyPerson> people2 = MyPerson.listByNameStartsWith(conn, 'An')
            MyPerson person = OrmRead.getById(conn, MyPerson.class, 1)


            MyPerson p = new MyPerson(name: 'Andrew')
            OrmErrorCollector collector = OrmWrite.save(conn, p)


        })
    }
}
