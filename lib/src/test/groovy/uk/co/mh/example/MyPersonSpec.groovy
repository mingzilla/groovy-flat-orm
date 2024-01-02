package uk.co.mh.example

import spock.lang.Specification

/**
 * @author ming.huang
 * @since 01/01/2024
 */
class MyPersonSpec extends Specification {

    def "Test creation"() {
        expect:
        new MyPerson() != null
    }
}
