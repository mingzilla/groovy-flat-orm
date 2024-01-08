package uk.co.mingzilla.example

import uk.co.mingzilla.flatorm.domain.OrmDomain
import uk.co.mingzilla.flatorm.domain.OrmMapping
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmValidate
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class MyPerson implements OrmDomain {

    Integer id
    String name

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'SERIAL'),
                OrmMapping.create('name', 'usercode'),
        ])
    }

    @Override
    DomainAndErrors validate() {
        DomainAndErrors item = DomainAndErrors.create(this)
        OrmValidate.required(item, ['id', 'name'])
        OrmValidate.ifSatisfies({ id == 1 }).minLength(item, ['name'], 5)
        return item;
    }

    @Override
    String resolveTableName() {
        return 'MIS_USERS'
    }

    static List<MyPerson> listByNameStartWith(Connection connection, String prefix) {
        String sql = """
        SELECT * 
        FROM MIS_USERS
        WHERE USERCODE like '${prefix}%'
        """
        return OrmRead.list(connection, MyPerson.class, sql)
    }
}
