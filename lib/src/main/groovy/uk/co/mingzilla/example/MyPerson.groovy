package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.definition.AbstractOrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.definition.OrmValidate
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection
import java.sql.PreparedStatement

import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.minLength
import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.required

/**
 * Refer to {@link AbstractOrmDomain} for all the available ORM default functions provided by this library.
 *
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class MyPerson implements OrmDomain {

    Integer id
    String name

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'serial'),
                OrmMapping.create('name', 'usercode'),
        ])
    }

    @Override
    OrmErrorCollector validate() {
        OrmErrorCollector item = OrmErrorCollector.create(this)
        OrmValidate.with(item, 'id', [required()])
        OrmValidate.with(item, 'name', [required()])
        OrmValidate.ifSatisfies({ id == 1 }).then(item, 'name', [minLength(5)])
        return item
    }

    @Override
    String tableName() {
        return 'mis_users'
    }

    static List<MyPerson> listByNameStartsWith(Connection connection, String prefix) {
        String sql = """
        select * 
        from mis_users
        where usercode like ?
        """
        return OrmRead.list(connection, MyPerson.class, sql, { PreparedStatement it ->
            it.setString(1, "${prefix}%")
            return it
        })
    }
}
