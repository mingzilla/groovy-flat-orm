package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.flatorm.domain.validation.DomainAndErrors
import uk.co.mingzilla.flatorm.domain.validation.DomainErrors

/**
 * @since 13/01/2024
 * @author ming.huang
 */
class OrmValidateSpec extends Specification {

    @Unroll
    void "Test required"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.required(item, ['name'])

        then:
        DomainErrors domainErrors = item.domainErrors
        assert domainErrors.hasErrors() != isValid
        assert domainErrors.hasNoErrors() == isValid

        where:
        field  | value  | isValid
        'name' | 'Andy' | true
        'name' | ' '    | false
    }

    @Unroll
    void "Test minLength"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.minLength(item, ['name'], 3)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field  | value  | isValid
        'name' | 'Andy' | true
        'name' | 'Yo'   | false
        'name' | null   | true // if field is required, use required for validation
    }

    @Unroll
    void "Test minValue, maxValue"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.minValue(item, ['age'], 18)
        OrmValidate.maxValue(item, ['age'], 80)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field | value | isValid
        'age' | 18    | true // minValue
        'age' | 17    | false
        'age' | null  | true

        'age' | 80    | true // maxValue
        'age' | 81    | false
    }

    @Unroll
    void "Test inList - text"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.inList(item, ['gender'], ['male', 'female'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field    | value  | isValid
        'gender' | 'male' | true
        'gender' | 'M'    | false
        'gender' | null   | true
    }

    @Unroll
    void "Test inList - number"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.inList(item, ['bornMonth'], 1..12)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field       | value | isValid
        'bornMonth' | 1     | true
        'bornMonth' | 12    | true
        'bornMonth' | 0     | false
        'bornMonth' | 13    | false
        'bornMonth' | null  | true
    }

    @Unroll
    void "Test notInList - text"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.notInList(item, ['gender'], ['male', 'female'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field    | value  | isValid
        'gender' | 'male' | false
        'gender' | 'M'    | true
        'gender' | null   | true
    }

    @Unroll
    void "Test notInList - number"() {
        given:
        DomainAndErrors item = DomainAndErrors.create(new Person([(field): (value)]))

        when:
        OrmValidate.notInList(item, ['bornMonth'], 1..12)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        field       | value | isValid
        'bornMonth' | 1     | false
        'bornMonth' | 12    | false
        'bornMonth' | 0     | true
        'bornMonth' | 13    | true
        'bornMonth' | null  | true
    }

    @Unroll
    void "Test ifHaving"() {
        given:
        Person person = new Person()
        person.name = name
        person.age = age
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifHaving('name').required(item, ['age'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        name   | age  | isValid
        'Andy' | 20   | true
        'Andy' | null | false
        null   | null | true
        null   | 20   | true
    }

    @Unroll
    void "Test ifNotHaving"() {
        given:
        Person person = new Person()
        person.name = name
        person.age = age
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifNotHaving('name').required(item, ['age'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        name   | age  | isValid
        'Andy' | 20   | true
        'Andy' | null | true
        null   | null | false
        null   | 20   | true
    }

    @Unroll
    void "Test ifSatisfies - required"() {
        given:
        Person person = new Person()
        person.name = name
        person.age = age
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifSatisfies({ age > 35 }).required(item, ['name'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        age  | name   | isValid
        40   | 'Andy' | true
        40   | null   | false

        20   | 'Andy' | true
        20   | null   | true
        null | 'Andy' | true
        null | null   | true
    }

    @Unroll
    void "Test ifSatisfies - minLength"() {
        given:
        Person person = new Person()
        person.name = name
        person.age = age
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifSatisfies({ age > 35 }).minLength(item, ['name'], 3)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        age  | name   | isValid
        40   | 'Andy' | true
        40   | 'Yo'   | false
        40   | null   | true

        20   | 'Andy' | true
        20   | null   | true
        null | 'Andy' | true
        null | null   | true
    }

    @Unroll
    void "Test ifSatisfies - minValue, maxValue"() {
        given:
        Person person = new Person()
        person.name = name
        person.age = age
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).minValue(item, ['age'], 18)
        OrmValidate.ifSatisfies({ name == 'Andy' }).maxValue(item, ['age'], 80)

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        name   | age  | isValid
        'Andy' | 18   | true
        'Andy' | 17   | false
        'Andy' | null | true
        'Andy' | 80   | true
        'Andy' | 81   | false

        'Bob'  | 18   | true
        'Bob'  | 17   | true
        'Bob'  | null | true
        'Bob'  | 80   | true
        'Bob'  | 81   | true
    }

    @Unroll
    void "Test ifSatisfies - inList"() {
        given:
        Person person = new Person()
        person.name = name
        person.gender = gender
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).inList(item, ['gender'], ['male', 'female'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        name   | gender | isValid
        'Andy' | 'male' | true
        'Andy' | 'M'    | false
        'Andy' | null   | true

        'Bob'  | 'male' | true
        'Bob'  | 'M'    | true
        'Bob'  | null   | true
    }

    @Unroll
    void "Test ifSatisfies - notInList"() {
        given:
        Person person = new Person()
        person.name = name
        person.gender = gender
        DomainAndErrors item = DomainAndErrors.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).notInList(item, ['gender'], ['male', 'female'])

        then:
        assert item.domainErrors.hasNoErrors() == isValid

        where:
        name   | gender | isValid
        'Andy' | 'male' | false
        'Andy' | 'M'    | true
        'Andy' | null   | true

        'Bob'  | 'male' | true
        'Bob'  | 'M'    | true
        'Bob'  | null   | true
    }

    @CompileStatic
    private class Person implements OrmDomain {

        String name
        Integer age
        String gender
        Integer bornMonth

        @Override
        List<OrmMapping> resolveMappings() {
            return OrmMapping.mapDomain(Person.class, [])
        }

        @Override
        DomainAndErrors validate() {
            // Example implementation of a validate function
            DomainAndErrors item = DomainAndErrors.create(this)

            OrmValidate.required(item, ['name'])
            OrmValidate.minLength(item, ['name'], 3)
            OrmValidate.minValue(item, ['age'], 18)
            OrmValidate.maxValue(item, ['age'], 80)

            OrmValidate.inList(item, ['gender'], ['male', 'female'])
            OrmValidate.notInList(item, ['age'], 60..64)

            OrmValidate.ifHaving('name').required(item, ['age'])
            return item
        }

        @Override
        String resolveTableName() {
            return 'PERSON'
        }
    }
}
