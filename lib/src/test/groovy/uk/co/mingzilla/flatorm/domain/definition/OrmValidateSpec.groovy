package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.*

/**
 * @since 14/01/2024
 * @author ming.huang
 */
class OrmValidateSpec extends Specification {

    @Unroll
    void "Test required"() {
        given:
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'name', [required()])

        then:
        assert item.hasErrors() != isValid

        where:
        field  | value  | isValid
        'name' | ' '    | false
        'name' | 'Andy' | true
    }

    @Unroll
    void "Test minLength"() {
        given:
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'name', [minLength(3)])

        then:
        assert item.hasErrors() != isValid

        where:
        field  | value  | isValid
        'name' | 'Andy' | true
        'name' | 'Yo'   | false
        'name' | null   | true // if field is required, use required for validation
    }

    @Unroll
    void "Test minValue, maxValue"() {
        given:
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'age', [minValue(18), maxValue(80)])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'gender', [inList(['male', 'female'])])

        then:
        assert item.hasErrors() != isValid

        where:
        field    | value  | isValid
        'gender' | 'male' | true
        'gender' | 'M'    | false
        'gender' | null   | true
    }

    @Unroll
    void "Test inList - number"() {
        given:
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'bornMonth', [inList(1..12)])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'gender', [notInList(['male', 'female'])])

        then:
        assert item.hasErrors() != isValid

        where:
        field    | value  | isValid
        'gender' | 'male' | false
        'gender' | 'M'    | true
        'gender' | null   | true
    }

    @Unroll
    void "Test notInList - number"() {
        given:
        OrmErrorCollector item = OrmErrorCollector.create(new Person([(field): (value)]))

        when:
        OrmValidate.with(item, 'bornMonth', [notInList(1..12)])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifHaving('name').then(item, 'age', [required()])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifNotHaving('name').then(item, 'age', [required()])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifSatisfies({ age > 35 }).then(item, 'name', [required()])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifSatisfies({ age > 35 }).then(item, 'name', [minLength(3)])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).then(item, 'age', [minValue(18), maxValue(80)])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).then(item, 'gender', [inList(['male', 'female'])])

        then:
        assert item.hasErrors() != isValid

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
        OrmErrorCollector item = OrmErrorCollector.create(person)

        when:
        OrmValidate.ifSatisfies({ name == 'Andy' }).then(item, 'gender', [notInList(['male', 'female'])])

        then:
        assert item.hasErrors() != isValid

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
    private static class Person implements OrmDomain {

        Integer id
        String name
        Integer age
        String gender
        Integer bornMonth

        @Override
        List<OrmMapping> resolveMappings() {
            return OrmMapping.mapDomain(Person.class, [])
        }

        @Override
        OrmErrorCollector validate() {
            // Example implementation of a validate function
            OrmErrorCollector item = OrmErrorCollector.create(this)

            OrmValidate.with(item, 'name', [required(), minLength(3)])
            OrmValidate.with(item, 'age', [minValue(18), maxValue(80), notInList(60..64)])
            OrmValidate.with(item, 'gender', [inList(['male', 'female'])])
            OrmValidate.ifHaving('name').then(item, 'age', [required()])

            return item
        }

        @Override
        String tableName() {
            return 'PERSON'
        }
    }
}
