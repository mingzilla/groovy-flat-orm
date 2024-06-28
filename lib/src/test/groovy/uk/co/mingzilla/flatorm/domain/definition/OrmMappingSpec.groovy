package uk.co.mingzilla.flatorm.domain.definition

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
                OrmMapping.create('id', 'serial'),
        ])

        then:
        items.camelFieldName.containsAll(['id', 'name'])
        items.dbFieldName.containsAll(['serial', 'name'])
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
        "name"         | "name"
        "age"          | "age"
        "address"      | "address"
    }

    def "test mapDomain with default mappings"() {
        given:
        List<OrmMapping> expectedMappings = [
                OrmMapping.create("name", "name"),
                OrmMapping.create("age", "age"),
                OrmMapping.create("active", "active")
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
                OrmMapping.create("name", "name"),
                OrmMapping.create("age", "age"),
                OrmMapping.create("active", "active")
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
        resultSet.getObject("name") >> "John"
        resultSet.getObject("age") >> 25
        resultSet.getObject("active") >> true

        List<OrmMapping> mappings = [
                OrmMapping.create("name", "name"),
                OrmMapping.create("age", "age"),
                OrmMapping.create("active", "active")
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
