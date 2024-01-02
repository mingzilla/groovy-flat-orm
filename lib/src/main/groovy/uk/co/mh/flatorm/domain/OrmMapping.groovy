package uk.co.mh.flatorm.domain

import uk.co.mh.flatorm.util.Fn

import java.sql.ResultSet

/**
 * @since 01/01/2024
 * @author ming.huang
 */
class OrmMapping {

    String camelFieldName
    String dbFieldName

    static OrmMapping create(String camelFieldName, String dbFieldName) {
        new OrmMapping(
                camelFieldName: camelFieldName,
                dbFieldName: dbFieldName,
        )
    }

    /**
     * Map the whole domain object, which allows custom mapping to override the default (dbField: snake case, domainField: camel case, id: maps to serial).
     * */
    static List<OrmMapping> mapDomain(Class aClass, List<OrmMapping> customMapping = null) {
        List<OrmMapping> defaults = createDomainDefault(aClass)
        List<OrmMapping> items = (customMapping && !customMapping.empty) ? (customMapping + defaults) : defaults
        return items.unique { it.camelFieldName }
    }

    private static List<OrmMapping> createDomainDefault(Class aClass) {
        Object obj = aClass.newInstance() // create object regardless if it defines private constructor
        Map map = Fn.toMap(obj)
        List<String> fields = map.keySet() as List<String>

        fields.collect { String field ->
            String dbFieldName = Fn.camelToUpperSnakeCase(field)
            return create(field, dbFieldName)
        }
    }

    static <T> T toDomain(List<OrmMapping> dbDomainFieldMappings, ResultSet resultSet, Closure<T> createDomainFn) {
        Map props = dbDomainFieldMappings.collectEntries { OrmMapping mapping ->
            String key = mapping.camelFieldName
            String value = Fn.safeGet(null)({ resultSet.getObject(mapping.dbFieldName) })
            [(key): (value)]
        }

        return createDomainFn(props)
    }
}
