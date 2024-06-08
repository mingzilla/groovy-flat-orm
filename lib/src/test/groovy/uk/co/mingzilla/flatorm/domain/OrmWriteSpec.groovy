package uk.co.mingzilla.flatorm.domain

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.PreparedStatement

class OrmWriteSpec extends Specification {

    class MyPerson implements OrmDomain {
        boolean booleanField
        Boolean boolean2Field

        BigDecimal bigDecimalField

        Date dateField

        double doubleField
        Double double2Field

        float floatField
        Float float2Field

        int idField
        Integer id

        long longField
        Long long2Field

        String name

        @Override
        List<OrmMapping> resolveMappings() {
            return OrmMapping.mapDomain(MyPerson.class, [])
        }

        @Override
        OrmErrorCollector validate() {
            return null
        }

        @Override
        String tableName() {
            return 'people'
        }
    }

    def "Test setStatementParams method"() {
        given:
        OrmDomain person = new MyPerson(
                id: 1,
                booleanField: true,
                boolean2Field: false,
                bigDecimalField: 100,
                dateField: new Date(),
                doubleField: 2.20,
                double2Field: 4.20,
                floatField: 1.20,
                float2Field: 3.20,
                idField: 5,
                longField: 11L,
                long2Field: 12L,
                name: 'John',
        )
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(person.resolveMappings())
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]

        // Mock PreparedStatement
        PreparedStatement statement = Mock(PreparedStatement)

        when:
        OrmWrite.setStatementParams(statement, person, nonIdMappings)

        then:
        1 * statement.setBigDecimal(1, person.bigDecimalField)
        1 * statement.setBoolean(2, person.boolean2Field)
        1 * statement.setBoolean(3, person.booleanField)
        1 * statement.setDate(4, person.dateField)
        1 * statement.setDouble(5, person.double2Field)
        1 * statement.setDouble(6, person.doubleField)
        1 * statement.setFloat(7, person.float2Field)
        1 * statement.setFloat(8, person.floatField)
        1 * statement.setInt(9, person.idField)
        1 * statement.setLong(10, person.long2Field)
        1 * statement.setLong(11, person.longField)
        1 * statement.setString(12, person.name)
    }

    def "Test createInsertStatement method"() {
        given:
        String tableName = "MY_TABLE"
        List<OrmMapping> nonIdMappings = [
                new OrmMapping(camelFieldName: "name", dbFieldName: "Name"),
                new OrmMapping(camelFieldName: "age", dbFieldName: "Age")
        ]

        when:
        String insertStatement = OrmWrite.createInsertStatement(tableName, nonIdMappings)

        then:
        insertStatement == "insert into my_table (Name, Age) values (?, ?)"
    }

    def "Test createUpdateStatement method"() {
        given:
        String tableName = "MY_TABLE"
        Integer id = 1
        OrmMapping idMapping = new OrmMapping(camelFieldName: "id", dbFieldName: "ID")
        List<OrmMapping> nonIdMappings = [
                new OrmMapping(camelFieldName: "name", dbFieldName: "Name"),
                new OrmMapping(camelFieldName: "age", dbFieldName: "Age")
        ]

        when:
        String updateStatement = OrmWrite.createUpdateStatement(tableName, id, idMapping, nonIdMappings)

        then:
        updateStatement == "update my_table set Name = ?, Age = ? where ID = 1"
    }
}
