package uk.co.mingzilla.flatorm.util

import spock.lang.Specification

/**
 * @since 07/01/2024
 * @author ming.huang
 */
class FnSpec extends Specification {

    def 'isNumber'() {
        expect:
        assert !Fn.isNumber(null)
        assert !Fn.isNumber('')
        assert !Fn.isNumber('  ')
        assert !Fn.isNumber('ABC')
        assert !Fn.isNumber('+ABC')
        assert Fn.isNumber('12')
        assert Fn.isNumber('12.1')
        assert Fn.isNumber('0.123456789123546789')
        assert Fn.isNumber('123456789123546789')
        assert Fn.isNumber('123456789123546789.00')
    }
}
