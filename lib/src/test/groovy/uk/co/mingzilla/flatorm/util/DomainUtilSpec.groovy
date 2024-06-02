package uk.co.mingzilla.flatorm.util

import spock.lang.Specification
import spock.lang.Unroll

class DomainUtilSpec extends Specification {

    static class TestDomain {
        String name
        int age
        boolean active
    }

    @Unroll
    def "test mergeFields with newProps #newProps"() {
        given:
        TestDomain obj = new TestDomain(name: "John", age: 25, active: true)

        when:
        DomainUtil.mergeFields(obj, newProps)

        then:
        obj.name == expectedName
        obj.age == expectedAge
        obj.active == expectedActive

        where:
        newProps                               | expectedName | expectedAge | expectedActive
        [name: "Jane", age: 30, active: false] | "Jane"       | 30          | false
        [name: " "]                            | ""           | 25          | true
        [age: 40]                              | "John"       | 40          | true
        [active: false]                        | "John"       | 25          | false
        [:]                                    | "John"       | 25          | true
        null                                   | "John"       | 25          | true
    }

    @Unroll
    def "test mergeFields with null and empty strings"() {
        given:
        TestDomain obj = new TestDomain(name: "Initial", age: 25, active: true)

        when:
        DomainUtil.mergeFields(obj, newProps)

        then:
        obj.name == expectedName
        obj.age == expectedAge
        obj.active == expectedActive

        where:
        newProps                   | expectedName | expectedAge | expectedActive
        [name: null]               | null         | 25          | true
        [name: "  ", age: null]    | ""           | 25          | true
        [name: "New Name", age: 0] | "New Name"   | 0           | true
    }
}
