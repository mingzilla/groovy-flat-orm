package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.ResultSet

/**
 * @since 01/01/2024
 * @author ming.huang
 */
@CompileStatic
class OrmMapping {

    String camelFieldName
    String dbFieldName

    static OrmMapping create(String camelFieldName, String dbFieldName) {
        new OrmMapping(
                camelFieldName: camelFieldName,
                dbFieldName: dbFieldName.toLowerCase(),
        )
    }

    /**
     * Map the whole domain object, which allows custom mapping to override the default (dbField: snake case, domainField: camel case, id: maps to serial).
     * */
    static List<OrmMapping> mapDomain(Class aClass, List<OrmMapping> customMapping = null) {
        List<OrmMapping> defaults = createDomainDefault(aClass)
        List<OrmMapping> items = (customMapping && !customMapping.empty) ? (customMapping + defaults) : defaults
        return items.unique { it.camelFieldName }.sort { a, b -> a.dbFieldName <=> b.dbFieldName }
    }

    private static List<OrmMapping> createDomainDefault(Class aClass) {
        Object obj = aClass.newInstance() // create object regardless if it defines private constructor
        Map map = InFn.toMap(obj)
        List<String> fields = map.keySet() as List<String>

        fields.collect { String field ->
            String dbFieldName = InFn.camelToUpperSnakeCase(field)
            return create(field, dbFieldName)
        }
    }

    static <T> T toDomain(List<OrmMapping> dbDomainFieldMappings, ResultSet resultSet, Closure<T> createDomainFn) {
        Map props = dbDomainFieldMappings.collectEntries { OrmMapping mapping ->
            String key = mapping.camelFieldName
            String value = InFn.<String> safeGet(null, { resultSet.getObject(mapping.dbFieldName) })
            [(key): (value)]
        }

        return createDomainFn(props)
    }

    static List<List<OrmMapping>> splitIdAndNonIdMappings(List<OrmMapping> mappings) {
        OrmMapping idMapping = mappings.find { it.camelFieldName?.equalsIgnoreCase('id') }
        List<OrmMapping> nonIdMappings = mappings.findAll { it.camelFieldName != idMapping?.camelFieldName }
        return [[idMapping], nonIdMappings]
    }

    static OrmMapping getIdMapping(List<OrmMapping> mappings) {
        List<List<OrmMapping>> idAndNonIdMappings = splitIdAndNonIdMappings(mappings)
        return idAndNonIdMappings[0][0]
    }
}
