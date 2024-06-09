package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.IdGen

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class MyApp {

    static void main(String[] args) {
        runWithTx()
        runWithoutTx()
    }

    static void runWithTx() {
        Map errorMap = [:]
        OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            println 'runInTx'
            IdGen idGen = IdGen.create() // <-

            println OrmRead.count(conn, MyPerson.class)
            OrmErrorCollector collector1 = OrmWrite.validateAndSave(conn, new MyPerson(id: idGen.int, name: 'Bobby')) // <- success
            println OrmRead.count(conn, MyPerson.class)

            MyPerson p = new MyPerson(name: 'Christine')
            OrmErrorCollector collector2 = OrmWrite.validateAndSave(conn, p) // <- failure
            println OrmRead.count(conn, MyPerson.class)

            List<OrmErrorCollector> people = [collector1, collector2]
            boolean haveErrors = OrmErrorCollector.haveErrors([people])
            if (haveErrors) {
                errorMap = [people: OrmErrorCollector.toErrorMaps(people)]
                OrmActor.terminate() // <- trigger rollback, so that Bobby is not saved
            }
        })

        // when used in a controller, this can be returned as an API response
        println errorMap // [people:[[id:[[field:id, constraint:REQUIRED, invalidValue:null]]]]]
    }

    static void runWithoutTx() {
        OrmActor.run(RepoDb.conn, { Connection conn ->
            println 'run'
            IdGen idGen = IdGen.create() // <-
            List<MyPerson> people1 = OrmRead.listAll(conn, MyPerson.class) // <- Example usage
            List<MyPerson> people2 = MyPerson.listByNameStartsWith(conn, 'An') // <-
            MyPerson person = OrmRead.getById(conn, MyPerson.class, 1) // <-

            println OrmRead.count(conn, MyPerson.class) // <-
            println people1*.name.join(', ')
            println people2*.name.join(', ')
            println person?.name

            MyPerson p = new MyPerson(id: idGen.int, name: 'Andrew')
            OrmErrorCollector collector = OrmWrite.validateAndSave(conn, p) // <-

            println p.id
            println collector.hasErrors() // <-
            println OrmRead.count(conn, MyPerson.class)

            boolean isDeleted = OrmWrite.delete(conn, p) // <-
            println isDeleted
            println OrmRead.count(conn, MyPerson.class)
        })
    }
}
