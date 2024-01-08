package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmDomain
import uk.co.mingzilla.flatorm.domain.OrmMapping
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmValidate
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors
import uk.co.mingzilla.flatorm.domain.validation.DomainErrors

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class MyPerson implements OrmDomain {

    Integer id
    String name

    static List<MyPerson> listStartWith(Connection connection, String prefix) {
        String sql = """
        SELECT * 
        FROM MIS_USERS
        WHERE USERCODE like '${prefix}%'
        """
        return OrmRead.list(connection, sql, MyPerson.class)
    }

    static MyPerson getById(Connection connection, Integer id) {
        return OrmRead.getById(connection, MyPerson.class, id)
    }

    static Long count(Connection connection) {
        return OrmRead.count(connection, MyPerson.class)
    }

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'SERIAL'),
                OrmMapping.create('name', 'usercode'),
        ])
    }

    @Override
    DomainErrors validate() {
        DomainAndErrors item = DomainAndErrors.create(this)
        OrmValidate.required(item, ['id', 'name'])
        OrmValidate.whenSatisfies({ id == 1 }).minLength(item, ['name'], 5)
        return item.domainErrors;
    }

    @Override
    String resolveTableName() {
        return 'MIS_USERS'
    }
}
