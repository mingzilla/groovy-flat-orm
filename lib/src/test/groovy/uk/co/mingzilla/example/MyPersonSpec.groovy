package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

/**
 * @author ming.huang
 * @since 01/01/2024
 */
class MyPersonSpec extends Specification {

    def "Test creation"() {
        expect:
        new MyPerson() != null
    }

    void "Test validate"() {
        given:
        MyPerson person = new MyPerson(id: 1, name: 'Andy')

        when:
        OrmErrorCollector domainErrors = person.validate()

        then:
        assert domainErrors.hasErrors()
        assert domainErrors.toMap() == [
                'name': [
                        [constraint: 'MINIMUM_LENGTH', constraintValue: '5', field: 'name', invalidValue: 'Andy']
                ]
        ]
    }
}
