package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.DomainUtil
import uk.co.mingzilla.flatorm.util.IdGen
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.Connection

/**
 * Mimics how it works when using it in a controller
 */
class MyPersonController {

    List<Map> index() {
        List<Map> maps = OrmActor.run(RepoDb.conn, { Connection conn ->
            List<MyPerson> people = OrmRead.listAll(conn, MyPerson.class)
            return people.collect(InFn.&toMap)
        })
        return maps
    }

    Map get(Integer id) {
        Map resp = OrmActor.run(RepoDb.conn, { Connection conn ->
            MyPerson person = OrmRead.getById(conn, MyPerson.class, id)
            return InFn.toMap(person)
        })
        return resp
    }

    Map post(Map params) {
        Map resp = OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            IdGen idGen = IdGen.create()
            MyPerson item = new MyPerson(id: idGen.int)
            MyPerson person = DomainUtil.mergeRequestData(item, params, params)
            OrmErrorCollector collector = OrmWrite.validateAndSave(conn, p)

            if (collector.hasErrors()) {
                return collector.toMap()
            } else {
                return InFn.toMap(person)
            }
        })
        return resp
    }

    Map put(Integer id, Map params) {
        Map resp = OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            MyPerson item = OrmRead.getById(conn, MyPerson.class, id)
            MyPerson person = DomainUtil.mergeRequestData(item, params, params)
            OrmErrorCollector collector = OrmWrite.validateAndSave(conn, p)

            if (collector.hasErrors()) {
                return collector.toMap()
            } else {
                return InFn.toMap(person)
            }
        })
        return resp
    }

    boolean delete(Integer id) {
        boolean isDeleted = OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            MyPerson item = OrmRead.getById(conn, MyPerson.class, id)
            return OrmWrite.delete(conn, item)
        })
        return isDeleted
    }
}
