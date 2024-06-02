package uk.co.mingzilla.flatorm.domain

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.example.MyPerson

import java.sql.ResultSet

/**
 * @since 02/01/2024
 * @author ming.huang
 */
class OrmMappingSpec extends Specification {

    def "Test mapDomain"() {
        when:
        List<OrmMapping> items = OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'SERIAL'),
        ])

        then:
        items.camelFieldName.containsAll(['id', 'name'])
        items.dbFieldName.containsAll(['SERIAL', 'NAME'])
    }

    @Unroll
    def "test create method with camelFieldName: #camelFieldName and dbFieldName: #dbFieldName"() {
        when:
        OrmMapping ormMapping = OrmMapping.create(camelFieldName, dbFieldName)

        then:
        ormMapping.camelFieldName == camelFieldName
        ormMapping.dbFieldName == dbFieldName

        where:
        camelFieldName | dbFieldName
        "name"         | "NAME"
        "age"          | "AGE"
        "address"      | "ADDRESS"
    }

    def "test mapDomain with default mappings"() {
        given:
        List<OrmMapping> expectedMappings = [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        List<OrmMapping> mappings = OrmMapping.mapDomain(TestDomain.class)

        then:
        mappings.size() == expectedMappings.size()
        mappings*.camelFieldName.containsAll(expectedMappings*.camelFieldName)
        mappings*.dbFieldName.containsAll(expectedMappings*.dbFieldName)
    }

    def "test mapDomain with custom mappings"() {
        given:
        List<OrmMapping> customMappings = [OrmMapping.create("customField", "CUSTOM_FIELD")]
        List<OrmMapping> expectedMappings = customMappings + [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        List<OrmMapping> mappings = OrmMapping.mapDomain(TestDomain.class, customMappings)

        then:
        mappings.size() == expectedMappings.size()
        mappings*.camelFieldName.containsAll(expectedMappings*.camelFieldName)
        mappings*.dbFieldName.containsAll(expectedMappings*.dbFieldName)
    }

    def "test toDomain method"() {
        given:
        ResultSet resultSet = Mock(ResultSet)
        resultSet.getObject("NAME") >> "John"
        resultSet.getObject("AGE") >> 25
        resultSet.getObject("ACTIVE") >> true

        List<OrmMapping> mappings = [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        TestDomain domain = OrmMapping.toDomain(mappings, resultSet, { props -> new TestDomain(props) })

        then:
        domain.name == "John"
        domain.age == 25
        domain.active
    }

    static class TestDomain {
        String name
        Integer age
        Boolean active

        TestDomain(Map<String, Object> props) {
            if (props) {
                this.name = props['name']
                this.age = props['age'] as int
                this.active = props['active'] as boolean
            }
        }
    }
}
