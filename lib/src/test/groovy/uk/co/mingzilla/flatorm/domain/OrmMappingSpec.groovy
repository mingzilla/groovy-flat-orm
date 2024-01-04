package uk.co.mingzilla.flatorm.domain

import spock.lang.Specification
import uk.co.mingzilla.example.MyPerson

/**
 * @since 02/01/2024
 * @author ming.huang
 */
class OrmMappingSpec extends Specification {

    def "Test mapDomain"() {
        when:
        List<OrmMapping> items = OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'SERIAL'),
        ])

        then:
        items.camelFieldName.containsAll(['id', 'name'])
        items.dbFieldName.containsAll(['SERIAL', 'NAME'])
    }
}
