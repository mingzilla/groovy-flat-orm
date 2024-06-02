package uk.co.mingzilla.flatorm.util

import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class InFnSpec extends Specification {

    @Unroll
    def "test asBoolean"() {
        expect:
        InFn.asBoolean(input) == expected

        where:
        input     || expected
        "true"    || true
        " false " || false
        null      || null
    }

    @Unroll
    def "test asBigDecimal"() {
        expect:
        InFn.asBigDecimal(input) == expected

        where:
        input          || expected
        "123.45"       || new BigDecimal("123.45")
        "not a number" || null
    }

    @Unroll
    def "test asBigDecimalWithScale"() {
        expect:
        InFn.asBigDecimalWithScale(2, RoundingMode.HALF_UP, input) == expected

        where:
        input     || expected
        "123.456" || new BigDecimal("123.46")
        null      || null
    }

    @Unroll
    def "test asDouble"() {
        expect:
        InFn.asDouble(input) == expected

        where:
        input    || expected
        "123.45" || 123.45d
        "abc"    || null
    }

    @Unroll
    def "test asFloat"() {
        expect:
        InFn.asFloat(input) == expected

        where:
        input    || expected
        "123.45" || 123.45f
        "abc"    || null
    }

    @Unroll
    def "test asInteger"() {
        expect:
        InFn.asInteger(input) == expected

        where:
        input || expected
        "123" || 123
        "abc" || null
    }

    @Unroll
    def "test asLong"() {
        expect:
        InFn.asLong(input) == expected

        where:
        input    || expected
        "123456" || 123456L
        "abc"    || null
    }

    @Unroll
    def "test asString"() {
        expect:
        InFn.asString(input) == expected

        where:
        input       || expected
        "some text" || "some text"
        123         || "123"
        null        || null
    }

    @Unroll
    def "test safeGet"() {
        when:
        def result = InFn.safeGet(defaultValue, fn)

        then:
        result == expected

        where:
        defaultValue | fn                           || expected
        "default"    | { -> throw new Exception() } || "default"
        "default"    | { -> "value" }               || "value"
    }

    @Unroll
    def "test hasField"() {
        given:
        def map = [key: "value"]

        expect:
        InFn.hasField(fieldName, map) == expected

        where:
        fieldName || expected
        "key"     || true
        "missing" || false
    }

    @Unroll
    def "test isBigDecimal with input '#input'"() {
        expect:
        InFn.isBigDecimal(input) == expected

        where:
        input    | expected
        "123.45" | true
        "123"    | true
        "abc"    | false
        null     | false
    }

    @Unroll
    def "test isBigInteger with input '#input'"() {
        expect:
        InFn.isBigInteger(input) == expected

        where:
        input                  | expected
        "12345678901234567890" | true
        "123"                  | true
        "abc"                  | false
        null                   | false
    }

    @Unroll
    def "test isBoolean with input '#input'"() {
        expect:
        InFn.isBoolean(input) == expected

        where:
        input   | expected
        "true"  | true
        "false" | true
        "TRUE"  | true
        "yes"   | false
        null    | false
    }

    @Unroll
    def "test isDouble with input '#input'"() {
        expect:
        InFn.isDouble(input) == expected

        where:
        input    | expected
        "123.45" | true
        "123"    | true
        "abc"    | false
        null     | false
    }

    @Unroll
    def "test isFloat with input '#input'"() {
        expect:
        InFn.isFloat(input) == expected

        where:
        input    | expected
        "123.45" | true
        "123"    | true
        "abc"    | false
        null     | false
    }

    @Unroll
    def "test isInteger with input '#input'"() {
        expect:
        InFn.isInteger(input) == expected

        where:
        input    | expected
        "123"    | true
        "123.45" | false
        "abc"    | false
        null     | false
    }

    @Unroll
    def "test isLong with input '#input'"() {
        expect:
        InFn.isLong(input) == expected

        where:
        input                 | expected
        "1234567890123456789" | true
        "123"                 | true
        "abc"                 | false
        null                  | false
    }

    @Unroll
    def "test isNull with input '#input'"() {
        expect:
        InFn.isNull(input) == expected

        where:
        input  | expected
        null   | true
        "null" | false
        "abc"  | false
        ""     | false
    }

    @Unroll
    def "test isNumber with input '#input'"() {
        expect:
        InFn.isNumber(input) == expected

        where:
        input    | expected
        "123"    | true
        "123.45" | true
        "abc"    | false
        null     | false
    }

    enum TestEnum {
        ONE, TWO, THREE

        String name

        TestEnum(String name) {
            this.name = name
        }
    }

    @Unroll
    def "test getEnumKeys"() {
        expect:
        InFn.getEnumKeys(TestEnum) == ["name"]
    }

    @Unroll
    def "test getKeys"() {
        given:
        def obj = [name: "John", age: 30]

        expect:
        InFn.getKeys(obj) == ["name", "age"]
    }

    @Unroll
    def "test camelToUpperSnakeCase"() {
        expect:
        InFn.camelToUpperSnakeCase(input) == expected

        where:
        input           || expected
        "camelCaseText" || "CAMEL_CASE_TEXT"
    }

    @Unroll
    def "test propAsString"() {
        given:
        def obj = [name: "John"]

        expect:
        InFn.propAsString("name", obj) == "John"
        InFn.propAsString("age", obj) == null
    }

    @Unroll
    def "test camelToLowerHyphenCase"() {
        expect:
        InFn.camelToLowerHyphenCase(input) == expected

        where:
        input           || expected
        "camelCaseText" || "camel-case-text"
    }

    @Unroll
    def "test hyphenToSnakeCase"() {
        expect:
        InFn.hyphenToSnakeCase(input) == expected

        where:
        input              || expected
        "hyphen-case-text" || "hyphen_case_text"
    }

    @Unroll
    def "test snakeToHyphenCase"() {
        expect:
        InFn.snakeToHyphenCase(input) == expected

        where:
        input             || expected
        "snake_case_text" || "snake-case-text"
    }

    @Unroll
    def "test propAsInteger"() {
        given:
        def obj = [age: "30"]

        expect:
        InFn.propAsInteger("age", obj) == 30
        InFn.propAsInteger("height", obj) == null
    }

    @Unroll
    def "test toMap"() {
        given:
        def obj = [name: "John", age: 30]

        expect:
        InFn.toMap(obj) == [name: "John", age: 30]
    }

    @Unroll
    def "test prop"() {
        given:
        def obj = [name: "John"]

        expect:
        InFn.prop("name", obj) == "John"
        InFn.prop("age", obj) == null
    }

    class Person {
        Integer age
        Long height
        Boolean isActive
    }

    @Unroll
    def "test setPrimitiveField"() {
        given:
        //noinspection GroovyAssignabilityCheck
        def obj = new Person(age: 0, height: 0L, isActive: false)

        when:
        InFn.setPrimitiveField(obj, "age", 25)
        InFn.setPrimitiveField(obj, "height", 175L)
        InFn.setPrimitiveField(obj, "isActive", true)

        then:
        obj.age == 25
        obj.height == 175L
        obj.isActive == true
    }

    @Unroll
    def "test spacedToLowerSnakeCase"() {
        expect:
        InFn.spacedToLowerSnakeCase(input) == expected

        where:
        input       || expected
        "some text" || "some_text"
    }

    @Unroll
    def "test trimToEmptyIfIsString"() {
        expect:
        InFn.trimToEmptyIfIsString(input) == expected

        where:
        input      || expected
        "  text  " || "text"
        123        || 123
    }

    @Unroll
    def "test withoutChar"() {
        expect:
        InFn.withoutChar(input) == expected

        where:
        input        || expected
        "abc123"     || "123"
        "no numbers" || " "
    }
}
