package uk.co.mh.example

import uk.co.mh.flatorm.domain.OrmDomain
import uk.co.mh.flatorm.domain.OrmMapping
import uk.co.mh.flatorm.domain.OrmRead

import java.sql.Connection

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class MyPerson implements OrmDomain {

    Integer id
    String name

    static List<MyPerson> listWithPrefix(Connection connection, String prefix) {
        String sql = """
        SELECT * 
        FROM MY_PERSON
        WHERE name like '${prefix}%'
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
        return OrmMapping.mapDomain(MyPerson.class, [])
    }

    @Override
    String resolveTableName() {
        return 'MY_PERSON'
    }
}
