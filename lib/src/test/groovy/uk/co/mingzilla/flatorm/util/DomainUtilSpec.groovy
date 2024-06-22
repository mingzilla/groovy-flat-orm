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

    static class TestDomain1 {
        int int1
        int int2
        int int3
        String text1
        String text2
    }

    @Unroll
    def "Test mergeRequestData"() {
        given:
        Map<String, Object> originalValues = [
                'int1' : 5, // user intentionally sets to 5
                'int2' : 5, // user intentionally sets to 5
                'int3' : null, // user intentionally sets to null
                'text1': null, // user intentionally sets to null
        ]
        Map<String, Object> values = [
                'int1' : 5, // user intentionally sets it to 5, and resolved as 5, use 5
                'int2' : 6, // user intentionally sets it to 5, but resolved as 6, use 6
                'int3' : 6, // user intentionally sets it to null, but resolved as 6 based on business logic, if only 6 is allowed, user cannot remove it
                'text1': null, // user intentionally sets it to null, and resolved as null, use null
                'text2': null, // user does not have an intention to set it to null, but resolved to null (this is null just because a variable is created without value), should use db value
        ]
        TestDomain1 hc = new TestDomain1([
                'int1' : 1,
                'int2' : 1,
                'int3' : 1,
                'text1': 'X',
                'text2': 'X',
        ])

        when:
        TestDomain1 newHc = DomainUtil.mergeRequestData(hc, values, originalValues)

        then:
        newHc.int1 == 5
        newHc.int2 == 6
        newHc.int3 == 6
        newHc.text1 == null
        newHc.text2 == 'X'
    }
}
