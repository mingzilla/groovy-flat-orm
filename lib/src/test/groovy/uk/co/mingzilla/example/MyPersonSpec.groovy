package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.validation.DomainErrors

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
        DomainErrors domainErrors = person.validate()

        then:
        assert domainErrors.hasErrors()
        assert !domainErrors.hasNoErrors()
        assert domainErrors.minLength == [name: 'Andy']
        assert domainErrors.errors() == [minLength: [name: 'Andy']]
    }
}
