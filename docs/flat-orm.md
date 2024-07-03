```python
"""in_fn"""
import decimal
import re
from datetime import date, time, datetime
from decimal import Decimal
from enum import Enum
from typing import Any, List, Optional, Callable, Type


class InFn:

    @staticmethod
    def as_string(obj: Any) -> str | None:
        return str(obj) if obj is not None else None

    @staticmethod
    def as_boolean(obj: Any) -> Optional[bool]:
        if obj is None:
            return None
        return InFn.as_string(obj).strip().lower() == 'true'

    @staticmethod
    def as_decimal(obj: Any) -> Optional[Decimal]:
        string_val = str(obj).strip()
        try:
            return Decimal(string_val)
        except decimal.InvalidOperation:
            return None

    @staticmethod
    def as_decimal_with_scale(decimal_places: Any, mode: Any, obj: Any) -> Optional[Decimal]:
        decimals = InFn.as_integer(decimal_places)
        decimal = InFn.as_decimal(obj)
        if decimal is not None and decimals is not None:
            return decimal.quantize(Decimal('1.' + '0' * decimals), rounding=mode)
        return decimal

    @staticmethod
    def as_double(obj: Any) -> Optional[float]:
        string_val = str(obj).strip()
        try:
            return float(string_val)
        except ValueError:
            return None

    @staticmethod
    def as_float(obj: Any) -> Optional[float]:
        string_val = str(obj).strip()
        try:
            return float(string_val)
        except ValueError:
            return None

    @staticmethod
    def as_integer(obj: Any) -> Optional[int]:
        string_val = str(obj).strip()
        try:
            return int(string_val)
        except ValueError:
            return None

    @staticmethod
    def as_long(obj: Any) -> Optional[int]:
        return InFn.as_integer(obj)

    @staticmethod
    def safe_get(default_value: Any, fn: Callable) -> Any:
        try:
            return fn()
        except Exception:
            return default_value

    @staticmethod
    def has_field(field_name: str, o: Any) -> bool:
        if o is None:
            return False
        if isinstance(o, dict):
            return field_name in o
        return hasattr(o, field_name)

    @staticmethod
    def is_blank(value: str) -> bool:
        return not bool(value and value.strip())

    @staticmethod
    def is_not_blank(value: str) -> bool:
        return bool(value and value.strip())

    @staticmethod
    def is_decimal(obj: Any) -> bool:
        return InFn.as_decimal(obj) is not None

    @staticmethod
    def is_boolean(obj: Any) -> bool:
        val = InFn.as_string(obj)
        if val is None: return False
        return val.lower() in ['true', 'false']

    @staticmethod
    def is_double(obj: Any) -> bool:
        return InFn.as_double(obj) is not None

    @staticmethod
    def is_float(obj: Any) -> bool:
        return InFn.as_float(obj) is not None

    @staticmethod
    def is_integer(obj: Any) -> bool:
        return InFn.as_integer(obj) is not None

    @staticmethod
    def is_none(v: Any) -> bool:
        return v is None

    @staticmethod
    def is_number(value: Any) -> bool:
        try:
            float(value)
            return True
        except (TypeError, ValueError):
            return False

    @staticmethod
    def get_enum_keys(a_class: type, custom_exclude_fields: List[str] = None) -> List[str]:
        excludes = (custom_exclude_fields or []) + ['__class__', '__doc__', '__module__', '__weakref__', '__members__', '__name__', '__qualname__']
        return [prop for prop in dir(a_class) if prop not in excludes and not callable(getattr(a_class, prop))]

    @staticmethod
    def get_keys(o: Any) -> List[str]:
        if o is None:
            return []
        if isinstance(o, type) and issubclass(o, Enum):
            return InFn.get_enum_keys(o)
        props = o if isinstance(o, dict) else vars(o)
        return [k for k in props.keys() if k != 'class']

    @staticmethod
    def get_static_field_type(clazz: Type[Any], field: str) -> Type[Any] | None:
        try:
            return clazz.__annotations__[field]
        except KeyError:
            return None

    @staticmethod
    def camel_to_upper_snake_case(text: str) -> Optional[str]:
        return re.sub(r'(?<!^)(?=[A-Z])', '_', text).upper().lstrip('_') if text else None

    @staticmethod
    def prop_as_string(name: str, obj: Any) -> Optional[str]:
        return InFn.as_string(InFn.prop(name, obj or {}))

    @staticmethod
    def camel_to_lower_hyphen_case(text: str) -> Optional[str]:
        return re.sub(r'(?<!^)(?=[A-Z])', '-', text).lower().lstrip('-') if text else None

    @staticmethod
    def hyphen_to_snake_case(text: str) -> Optional[str]:
        return text.replace('-', '_') if text else None

    @staticmethod
    def snake_to_hyphen_case(text: str) -> Optional[str]:
        return text.replace('_', '-') if text else None

    @staticmethod
    def prop_as_boolean(name: str, obj: Any) -> Optional[bool]:
        return InFn.as_boolean(InFn.prop_as_string(name, obj))

    @staticmethod
    def prop_as_decimal(name: str, obj: Any) -> Optional[Decimal]:
        return InFn.as_decimal(InFn.prop_as_string(name, obj))

    @staticmethod
    def prop_as_double(name: str, obj: Any) -> Optional[float]:
        return InFn.as_double(InFn.prop_as_string(name, obj))

    @staticmethod
    def prop_as_float(name: str, obj: Any) -> Optional[float]:
        return InFn.as_float(InFn.prop_as_string(name, obj))

    @staticmethod
    def prop_as_integer(name: str, obj: Any) -> Optional[int]:
        return InFn.as_integer(InFn.prop_as_string(name, obj))

    @staticmethod
    def prop_as_long(name: str, obj: Any) -> Optional[int]:
        return InFn.as_long(InFn.prop_as_string(name, obj))

    @staticmethod
    def self(x: Any) -> Any:
        return x

    @staticmethod
    def to_dict(o: Any, custom_exclude_fields: Optional[List[str]] = None) -> dict:
        exclude_fields = custom_exclude_fields or []
        keys = InFn.get_keys(o) if o else []

        result = {k: getattr(o, k) for k in keys if k not in exclude_fields}

        if 'id' not in exclude_fields and InFn.has_field('id', o):
            result['id'] = getattr(o, 'id')

        return result

    @staticmethod
    def prop(name: str, o: Any) -> Any:
        if name is None:
            return None
        if isinstance(o, dict):
            return o.get(name)
        return getattr(o, name, None)

    @staticmethod
    def to_date(value: Any) -> date:
        if isinstance(value, datetime):
            return value.date()
        elif isinstance(value, date):
            return value
        elif isinstance(value, time):
            # Create a datetime object with the current date and given time
            current_date = datetime.now().date()
            dt = datetime.combine(current_date, value)
            return dt.date()
        else:
            raise TypeError("Unsupported type. Expected date, time, or datetime object.")

    @staticmethod
    def to_time(value: Any) -> time:
        if isinstance(value, datetime):
            return value.time()
        else:
            raise TypeError("Unsupported type. Expected datetime object.")

    @staticmethod
    def to_datetime(value: Any) -> datetime:
        if isinstance(value, datetime):
            return value
        elif isinstance(value, date):
            # Combine date with default time (midnight)
            return datetime.combine(value, datetime.min.time())
        elif isinstance(value, time):
            # Create a date object (current date)
            current_date = datetime.now().date()
            # Combine date and time to create datetime
            return datetime.combine(current_date, value)
        else:
            raise TypeError("Unsupported type. Expected date, time, or datetime object.")

    @staticmethod
    def set_primitive_field(obj: Any, field_name: str, value: Any) -> Any:
        if obj is None or field_name is None:
            return obj

        if not InFn.has_field(field_name, obj):
            return obj

        if value is None:
            setattr(obj, field_name, None)
            return obj

        try:
            obj_fields = type(obj)()

            val_to_check = obj_fields.__dict__[field_name]

            if isinstance(val_to_check, int):
                setattr(obj, field_name, int(value))
            elif isinstance(val_to_check, float):  # after int check: int is float, float is not int
                setattr(obj, field_name, float(value))
            elif isinstance(val_to_check, bool):
                setattr(obj, field_name, bool(value))
            elif isinstance(val_to_check, str):
                setattr(obj, field_name, str(value))
            elif isinstance(val_to_check, datetime):
                setattr(obj, field_name, InFn.to_datetime(value))
            elif isinstance(val_to_check, date):  # after datetime check: datetime is date, date is not datetime
                setattr(obj, field_name, InFn.to_date(value))
            elif isinstance(val_to_check, time):
                setattr(obj, field_name, InFn.to_time(value))
            elif InFn.has_field(field_name, obj):
                setattr(obj, field_name, value)

        except TypeError:
            pass  # Field does not exist

        return obj

    @staticmethod
    def spaced_to_lower_snake_case(text: str) -> Optional[str]:
        return text.strip().lower().replace(" ", "_") if text else None

    @staticmethod
    def trim_to_empty_if_is_string(v: Any) -> Any:
        if not isinstance(v, str):
            return v
        return v.strip() if v is not None else None

    @staticmethod
    def without_char(obj: Any) -> str:
        if obj is None:
            return ''
        return re.sub(r'[a-zA-Z]+', '', str(obj))

```


```python
import random


class IdGen:
    __LIMIT = 10000
    __MIN_VALUE = -99999999
    __MAX_VALUE = -98999999

    def __init__(self):
        self.__generated_numbers = set()
        self.__random = random.Random()

    @staticmethod
    def create():
        return IdGen()

    def get_int(self):
        if len(self.__generated_numbers) >= IdGen.__LIMIT:
            raise Exception("All unique numbers have been generated")

        new_number = None
        while new_number is None or new_number in self.__generated_numbers:
            new_number = self.__random.randint(IdGen.__MIN_VALUE, IdGen.__MAX_VALUE)

        self.__generated_numbers.add(new_number)
        return new_number

    def clear(self):
        self.__generated_numbers.clear()

    @staticmethod
    def is_generated_id(num):
        try:
            id = int(num)
        except ValueError:
            return False
        return IdGen.__MIN_VALUE <= id <= IdGen.__MAX_VALUE

```


```python
from unittest import TestCase

from py_flat_orm.util.base_util.id_gen import IdGen


class TestIdGen(TestCase):
    def test_is_generated_id(self):
        id_gen = IdGen.create()
        id = id_gen.get_int()
        self.assertEqual(id_gen.is_generated_id(id), True)
        self.assertEqual(id_gen.is_generated_id(1), False)
        self.assertEqual(id_gen.is_generated_id("Hi"), False)

```



```python
from typing import Any, Dict, TypeVar

from .in_fn import InFn

T = TypeVar('T')


class DomainUtil:

    @staticmethod
    def merge_fields(obj: T, new_props: Dict[str, Any]) -> T:
        """
        Basic type includes: int, Integer, boolean, Boolean, String, Date
        String is trimToEmpty. Domain objects are supposed to be saved to db, A value should not just have empty space or space around.
        However, this doesn't do trimToNull for compatibility purposes. When working on existing bad code, trimToEmpty wouldn't make it hard for those devs.
        """
        new_props = new_props or {}
        relevant_props = {}

        for k, v in new_props.items():
            if hasattr(obj, k):
                relevant_props[k] = v

        for k, v in relevant_props.items():
            obj = InFn.set_primitive_field(obj, k, InFn.trim_to_empty_if_is_string(v))

        return obj

    @staticmethod
    def merge_request_data(obj: T, resolved_props: Dict[str, Any], unmodified_client_submitted_props: Dict[str, Any]) -> T:
        """
        Merge data submitted from the client side to the server side, which allows submitting only a single field to update one field using the API, without having to submit every single field.

        Used by Domain.mergeData(), so that setting data is consistently handled.
        This develops a consistent procedure, so that devs don't need to always consider if they need to use
        - e.g.`this.myField = myField` - set the value without fallback
        - or`this.myField = myField ?: this.myField` - picks up the db value if value supplied is null

        Scenarios:
        - if user intentionally sets value x, and resolved as x, use x
        - if user intentionally sets value x, and resolved as y, use y
        - if user intentionally sets value null, and resolved as y, use y
        - if user intentionally sets value null, and resolved as null, use null
        - if user does not submit field (no intent to change), and resolved as null (because a variable is created to process the logic), use db value
          - mostly occurs when using the API to update without supplying every single field
        """
        new_props = {}

        for k, v in resolved_props.items():
            client_sends_key = k in unmodified_client_submitted_props
            client_sets_null = unmodified_client_submitted_props.get(k) is None
            server_sets_value = v is not None
            has_field_and_set_to_null = client_sends_key and client_sets_null

            if server_sets_value:
                new_props[k] = v
            elif has_field_and_set_to_null:
                new_props[k] = None
            else:
                db_value = getattr(obj, k)
                new_props[k] = db_value

        return DomainUtil.merge_fields(obj, new_props)
```


```python
import unittest

from py_flat_orm.util.base_util.domain_util import DomainUtil


class TestDomain:
    def __init__(self, name='', age=0, active=False):
        self.name = name
        self.age = age
        self.active = active


class TestDomain1:
    def __init__(self, int1=0, int2=0, int3=0, text1='', text2=''):
        self.int1 = int1
        self.int2 = int2
        self.int3 = int3
        self.text1 = text1
        self.text2 = text2


class DomainUtilTest(unittest.TestCase):

    def test_merge_fields(self):
        new_props = [
            {"name": "Jane", "age": 30, "active": False},
            {"name": " "},
            {"age": 40},
            {"active": False},
            {},
            None
        ]
        expected_results = [
            ("Jane", 30, False),
            ("", 25, True),
            ("John", 40, True),
            ("John", 25, False),
            ("John", 25, True),
            ("John", 25, True)
        ]
        for new_prop, expected in zip(new_props, expected_results):
            with self.subTest(new_prop=new_prop):
                obj = TestDomain(name="John", age=25, active=True)
                DomainUtil.merge_fields(obj, new_prop)
                self.assertEqual((obj.name, obj.age, obj.active), expected)

    def test_merge_fields_with_none_and_empty_strings(self):
        new_props = [
            {"name": None},
            {"name": "  ", "age": None},
            {"name": "New Name", "age": 0}
        ]
        expected_results = [
            (None, 25, True),
            ("", None, True),
            ("New Name", 0, True)
        ]
        for new_prop, expected in zip(new_props, expected_results):
            with self.subTest(new_prop=new_prop):
                obj = TestDomain(name="John", age=25, active=True)
                DomainUtil.merge_fields(obj, new_prop)
                self.assertEqual((obj.name, obj.age, obj.active), expected)

    def test_merge_request_data(self):
        original_values = {
            'int1': 5,  # user intentionally sets to 5
            'int2': 5,  # user intentionally sets to 5
            'int3': None,  # user intentionally sets to none
            'text1': None  # user intentionally sets to none
        }
        values = {
            'int1': 5,  # user intentionally sets it to 5, and resolved as 5, use 5
            'int2': 6,  # user intentionally sets it to 5, but resolved as 6, use 6
            'int3': 6,  # user intentionally sets it to none, but resolved as 6 based on business logic, if only 6 is allowed, user cannot remove it
            'text1': None,  # user intentionally sets it to none, and resolved as none, use none
            'text2': None  # user does not have an intention to set it to none, but resolved to none (this is none just because a variable is created without value), should use db value
        }
        obj1 = TestDomain1(int1=1, int2=1, int3=1, text1='X', text2='X')  # values in the db
        new_obj = DomainUtil.merge_request_data(obj1, values, original_values)
        self.assertEqual(new_obj.int1, 5)
        self.assertEqual(new_obj.int2, 6)
        self.assertEqual(new_obj.int3, 6)
        self.assertIsNone(new_obj.text1)
        self.assertEqual(new_obj.text2, 'X')


if __name__ == '__main__':
    unittest.main()

```


```groovy
package uk.co.mingzilla.flatorm.util

import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.Driver

@CompileStatic
class ConnectionUtil {

    static Connection getConnection(String driverClassName, String url, Properties connectionProperties) {
        try {
            return ((Driver) Class.forName(driverClassName).newInstance()).connect(url, connectionProperties)
        } catch (Exception ex) {
            throw new RuntimeException(ex.message, ex)
        }
    }

    static void close(Connection connection) {
        try {
            connection?.close()
        } catch (Exception ignore) {
            // do nothing - don't mind if the close fails
        }
    }
}

```

```groovy
package uk.co.mingzilla.flatorm.domain.definition

import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

interface OrmDomain {

    List<OrmMapping> resolveMappings()

    OrmErrorCollector validate()

    Integer getId()

    void setId(Integer id)

    String tableName()
}
```

```groovy
package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.ResultSet

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

```

```python
from collections import OrderedDict
from typing import List, Callable, Type, TypeVar, Optional, Tuple

from py_flat_orm.util.base_util.in_fn import InFn  # type: ignore

T = TypeVar('T')


class OrmMapping:
    def __init__(self, camel_field_name: str, db_field_name: str):
        self.camel_field_name = camel_field_name
        self.db_field_name = db_field_name

    @classmethod
    def create(cls, camel_field_name: str, db_field_name: str) -> 'OrmMapping':
        return cls(camel_field_name, db_field_name.lower())

    @classmethod
    def map_domain(cls, a_class: Type, custom_mapping: Optional[List['OrmMapping']] = None) -> List['OrmMapping']:
        defaults = cls.create_domain_default(a_class)
        items = custom_mapping + defaults if custom_mapping else defaults
        unique_items = list(OrderedDict((item.camel_field_name, item) for item in items).values())
        return sorted(unique_items, key=lambda x: x.db_field_name)

    @classmethod
    def create_domain_default(cls, a_class: Type) -> List['OrmMapping']:
        obj = a_class()
        fields = InFn.to_dict(obj).keys()
        return [cls.create(field, InFn.camel_to_upper_snake_case(field)) for field in fields]

    @classmethod
    def to_domain(cls, db_domain_field_mappings: List['OrmMapping'], result_set, create_domain_fn: Callable[[dict], T]) -> T:
        props = {
            mapping.camel_field_name: InFn.safe_get(None, lambda: result_set.get(mapping.db_field_name))
            for mapping in db_domain_field_mappings
        }
        return create_domain_fn(props)

    @classmethod
    def split_id_and_non_id_mappings(cls, mappings: List['OrmMapping']) -> Tuple[List['OrmMapping'], List['OrmMapping']]:
        id_mapping = next((m for m in mappings if m.camel_field_name.lower() == 'id'), None)
        non_id_mappings = [m for m in mappings if m.camel_field_name != id_mapping.camel_field_name] if id_mapping else mappings
        return [id_mapping] if id_mapping else [], non_id_mappings

    @classmethod
    def get_id_mapping(cls, mappings: List['OrmMapping']) -> Optional['OrmMapping']:
        id_and_non_id_mappings = cls.split_id_and_non_id_mappings(mappings)
        return id_and_non_id_mappings[0][0] if id_and_non_id_mappings[0] else None

```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic

@CompileStatic
class OrmFieldError {

    OrmConstraint constraint // e.g., minValue 5
    String field // e.g., age
    Object invalidValue // e.g., 4

    static OrmFieldError create(OrmConstraint constraint, String field, Object invalidValue) {
        OrmFieldError item = new OrmFieldError()
        item.constraint = constraint
        item.field = field
        item.invalidValue = invalidValue
        return item
    }

    Map<String, Object> toMap() {
        Map<String, Object> m = [field: (field)] as Map<String, Object>
        m['constraint'] = constraint.type.value
        if (constraint.value != null) m['constraintValue'] = constraint.value
        if (constraint.values != null && !constraint.values.empty) m['constraintValues'] = constraint.values.join(', ')
        m['invalidValue'] = invalidValue
        return m
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic

@CompileStatic
class OrmFieldErrors {

    String field // e.g., age
    List<OrmFieldError> errors = []

    static OrmFieldErrors create(String field) {
        return new OrmFieldErrors(field: field)
    }

    OrmFieldErrors addError(OrmFieldError fieldError) {
        errors.add(fieldError)
        return this
    }

    boolean hasErrors() {
        return !errors.empty
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

enum OrmConstraintType {

    REQUIRED('REQUIRED'),
    MINIMUM_LENGTH('MINIMUM_LENGTH'),
    MINIMUM_VALUE('MINIMUM_VALUE'), // Have error calling it MIN_VALUE, so call it MINIMUM_VALUE
    MAXIMUM_VALUE('MAXIMUM_VALUE'),
    IN_LIST('IN_LIST'),
    NOT_IN_LIST('NOT_IN_LIST'),
    UNIQUE('UNIQUE')

    String value

    OrmConstraintType(String value) {
        this.value = value
    }
}
```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.util.InFn

@CompileStatic
class OrmConstraint {

    OrmConstraintType type // e.g., minValue
    String value // (optional) e.g., when minValue is 5, then 'type' is MINIMUM_VALUE, 'value' is set to 5
    List values // (optional) e.g., when inList is [1,2,3], then 'type' is IN_LIST, 'values' are [1,2,3]

    static OrmConstraint required() {
        return new OrmConstraint(type: OrmConstraintType.REQUIRED)
    }

    static OrmConstraint minLength(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MINIMUM_LENGTH, value: String.valueOf(value))
    }

    static OrmConstraint minValue(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MINIMUM_VALUE, value: String.valueOf(value))
    }

    static OrmConstraint maxValue(Integer value) {
        return new OrmConstraint(type: OrmConstraintType.MAXIMUM_VALUE, value: String.valueOf(value))
    }

    static OrmConstraint inList(List values) {
        return new OrmConstraint(type: OrmConstraintType.IN_LIST, values: values)
    }

    static OrmConstraint notInList(List values) {
        return new OrmConstraint(type: OrmConstraintType.NOT_IN_LIST, values: values)
    }

    static boolean isValid(OrmConstraint constraint, Object v) {
        switch (constraint.type) {
            case OrmConstraintType.REQUIRED:
                return StringUtils.isNotBlank(v as String)
            case OrmConstraintType.MINIMUM_LENGTH:
                return v == null || (String.valueOf(v ?: '').size() >= (constraint.value as Integer))
            case OrmConstraintType.MINIMUM_VALUE:
                return v == null || (InFn.isNumber(v) && InFn.asLong(v) >= (constraint.value as Integer))
            case OrmConstraintType.MAXIMUM_VALUE:
                return v == null || (InFn.isNumber(v) && InFn.asLong(v) <= (constraint.value as Integer))
            case OrmConstraintType.IN_LIST:
                return v == null || (v in constraint.values)
            case OrmConstraintType.NOT_IN_LIST:
                return v == null || (!(v in constraint.values))
            default:
                return true
        }
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmValidate

@CompileStatic
class OrmConditionalValidate {

    Closure<Boolean> conditionIsMetFn

    OrmErrorCollector then(OrmErrorCollector collector, String field, List<OrmConstraint> constraints) {
        if (!conditionIsMetFn(collector.domain)) return collector
        return OrmValidate.with(collector, field, constraints)
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.validation

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain

@CompileStatic
class OrmErrorCollector {

    OrmDomain domain
    Map<String, OrmFieldErrors> fields = [:] // key: name of a field, value: a collection of errors

    static OrmErrorCollector create(OrmDomain domain) {
        return new OrmErrorCollector(domain: domain)
    }

    void addError(OrmFieldError fieldError) {
        String field = fieldError.field
        if (!fields[(field)]) fields[(field)] = OrmFieldErrors.create(field)

        OrmFieldErrors fieldErrors = fields[(field)]
        fieldErrors.addError(fieldError)
    }

    boolean hasErrors() {
        return fields.find { it.value.hasErrors() } != null
    }

    static boolean haveErrors(List<List<OrmErrorCollector>> collectors) {
        OrmErrorCollector itemWithError = collectors.flatten().<OrmErrorCollector> toList().find { it?.hasErrors() }
        return itemWithError != null
    }

    static List<Map<String, List<Map>>> toErrorMaps(List<OrmErrorCollector> collectors) {
        List<OrmErrorCollector> itemWithError = collectors.<OrmErrorCollector> toList().findAll { it?.hasErrors() }
        return itemWithError*.toMap()
    }

    Map<String, List<Map>> toMap() {
        return fields.collectEntries {
            [(it.key): it.value.errors*.toMap()]
        }
    }
}

```

```groovy
package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import uk.co.mingzilla.flatorm.domain.validation.OrmConditionalValidate
import uk.co.mingzilla.flatorm.domain.validation.OrmConstraint
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.domain.validation.OrmFieldError
import uk.co.mingzilla.flatorm.util.InFn

@CompileStatic
class OrmValidate {

    static OrmErrorCollector with(OrmErrorCollector collector, String field, List<OrmConstraint> constraints) {
        Object value = collector.domain[(field)]
        constraints.each {
            collectError(collector, it, field, value)
        }
        return collector
    }

    private static OrmErrorCollector collectError(OrmErrorCollector collector, OrmConstraint constraint, String field, Object value) {
        if (OrmConstraint.isValid(constraint, value)) return collector

        OrmFieldError fieldError = OrmFieldError.create(constraint, field, value)
        collector.addError(fieldError)
        return collector
    }

    static OrmConditionalValidate ifHaving(String field) {
        Closure<Boolean> conditionIsMetFn = { OrmDomain it ->
            String v = InFn.propAsString(field, it)
            return StringUtils.isNotBlank(v)
        }
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }

    static OrmConditionalValidate ifNotHaving(String field) {
        Closure<Boolean> conditionIsMetFn = { OrmDomain it ->
            String v = InFn.propAsString(field, it)
            return StringUtils.isBlank(v)
        }
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }

    static OrmConditionalValidate ifSatisfies(Closure<Boolean> conditionIsMetFn) {
        return new OrmConditionalValidate(conditionIsMetFn: conditionIsMetFn)
    }
}

```

```python
from typing import List, Callable, Any

from py_flat_orm.domain.validation.orm_conditional_validate import OrmConditionalValidate  # type: ignore
from py_flat_orm.domain.validation.orm_constraint import OrmConstraint  # type: ignore
from py_flat_orm.domain.validation.orm_error_collector import OrmErrorCollector  # type: ignore
from py_flat_orm.domain.validation.orm_field_error import OrmFieldError  # type: ignore
from py_flat_orm.util.base_util.in_fn import InFn  # type: ignore
from .orm_domain import OrmDomain  # type: ignore


class OrmValidate:
    @staticmethod
    def with_rule(collector: OrmErrorCollector, field: str, constraints: List[OrmConstraint]) -> OrmErrorCollector:
        value = getattr(collector.domain, field, None)
        for constraint in constraints:
            OrmValidate.collect_error(collector, constraint, field, value)
        return collector

    @staticmethod
    def collect_error(collector: OrmErrorCollector, constraint: OrmConstraint, field: str, value: Any) -> OrmErrorCollector:
        if OrmConstraint.is_valid(constraint, value):
            return collector
        field_error = OrmFieldError.create(constraint, field, value)
        collector.add_error(field_error)
        return collector

    @staticmethod
    def if_having(field: str) -> OrmConditionalValidate:
        def condition_is_met_fn(domain: OrmDomain) -> bool:
            value = InFn.prop_as_string(field, domain)
            return InFn.is_not_blank(value)

        return OrmConditionalValidate(condition_is_met_fn)

    @staticmethod
    def if_not_having(field: str) -> OrmConditionalValidate:
        def condition_is_met_fn(domain: OrmDomain) -> bool:
            value = InFn.prop_as_string(field, domain)
            return InFn.is_blank(value)

        return OrmConditionalValidate(condition_is_met_fn)

    @staticmethod
    def if_satisfies(condition_is_met_fn: Callable[[OrmDomain], bool]) -> OrmConditionalValidate:
        return OrmConditionalValidate(condition_is_met_fn)

```


```groovy
package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.*

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

```



```groovy
package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection

@CompileStatic
abstract class AbstractOrmDomain<T extends AbstractOrmDomain<T>> implements OrmDomain {

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(this.class, [])
    }

    static <T extends AbstractOrmDomain<T>> Long count(Connection conn, Class<T> aClass) {
        return OrmRead.count(conn, aClass)
    }

    static <T extends AbstractOrmDomain<T>> List<T> listAll(Connection conn, Class<T> aClass) {
        return OrmRead.listAll(conn, aClass)
    }

    static <T extends AbstractOrmDomain<T>> T getById(Connection conn, Class<T> aClass, Integer id) {
        return OrmRead.getById(conn, aClass, id)
    }

    static <T extends AbstractOrmDomain<T>> T getFirst(Connection conn, Class<T> aClass, String selectStatement) {
        return OrmRead.getFirst(conn, aClass, selectStatement)
    }

    OrmErrorCollector validateAndSave(Connection conn) {
        return OrmWrite.validateAndSave(conn, this)
    }

    OrmDomain insertOrUpdate(Connection conn) {
        return OrmWrite.insertOrUpdate(conn, this)
    }

    boolean delete(Connection conn) {
        return OrmWrite.delete(conn, this)
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.util.DomainUtil
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
class OrmRead {

    static Closure<PreparedStatement> NO_PARAMS = { PreparedStatement it -> it }

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     */
    static <T> List<T> listAll(Connection conn, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String selectStatement = "select * from ${domain.tableName()}"
        return listAndMerge(conn, mappings, selectStatement, NO_PARAMS,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * Similar to {@link #listAll}. Intended to be used with a custom WHERE clause.
     */
    static <T> List<T> list(Connection conn, Class aClass, String selectStatement, Closure<PreparedStatement> setParamsFn) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return listAndMerge(conn, mappings, selectStatement, setParamsFn,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * List objects with a given select statement. Connection is not closed.
     * Always wraps the whole request and response with try/catch/finally close.
     */
    static <T> List<T> listAndMerge(Connection conn, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<PreparedStatement> setParamsFn, Closure<T> createDomainFn) {
        List<T> objs = []
        PreparedStatement statement
        ResultSet resultSet

        try {
            statement = conn.prepareStatement(selectStatement.toString())
            statement = setParamsFn(statement)
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                T domain = OrmMapping.toDomain(dbDomainFieldMappings, resultSet, createDomainFn)
                objs.add(domain)
            }
        } catch (SQLException sqlEx) {
            RuntimeException ex = new RuntimeException("Failed running select statement to create object: $sqlEx.message", sqlEx)
            throw ex
        }
        return objs
    }

    /**
     * When used, the select statement typically needs a WHERE clause.
     */
    static <T> T getById(Connection conn, Class aClass, def id) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        List<OrmMapping> mappings = domain.resolveMappings()

        String idField = mappings.find { it.camelFieldName == 'id' }?.dbFieldName
        String selectStatement = "SELECT * FROM ${domain.tableName()} WHERE ${idField} = ${id}"
        return getAndMerge(conn, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * When used, the select statement typically needs a WHERE clause.
     */
    static <T> T getFirst(Connection conn, Class aClass, String selectStatement) {
        List<OrmMapping> mappings = (aClass.newInstance() as OrmDomain).resolveMappings()

        return getAndMerge(conn, mappings, selectStatement,
                { Map props ->
                    Object obj = aClass.newInstance()
                    DomainUtil.mergeFields(obj, props) as T
                })
    }

    /**
     * Same as {@link #listAndMerge}, but only return the 1st object found
     */
    static <T> T getAndMerge(Connection conn, List<OrmMapping> dbDomainFieldMappings, String selectStatement, Closure<T> createDomainFn) {
        try {
            PreparedStatement statement = conn.prepareStatement(selectStatement.toString())
            ResultSet resultSet = statement.executeQuery()
            try {
                resultSet.next()
                return OrmMapping.toDomain(dbDomainFieldMappings, resultSet, createDomainFn)
            } catch (Exception ignore) {
                return null // if valid SQL doesn't have data, then return null
            }
        } catch (SQLException sqlEx) {
            RuntimeException ex = new RuntimeException("Failed running select statement to create object: $sqlEx.message", sqlEx)
            throw ex
        }
    }

    /**
     * Count table records with a given table name.
     */
    static Long count(Connection conn, Class aClass) {
        OrmDomain domain = aClass.newInstance() as OrmDomain
        String selectStatement = "select count(*) from ${domain.tableName()}".toString()
        return getCount(conn, selectStatement)
    }

    /**
     * Intended to be used for a SELECT count(*) statement, which also allows e.g. JOIN and WHERE clause.
     */
    private static Long getCount(Connection conn, String selectStatement) {
        PreparedStatement statement
        ResultSet resultSet

        Long count = 0
        try {
            statement = conn.prepareStatement(selectStatement)
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                count = InFn.asLong(resultSet.getObject(1))
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed running select statement to count records: " + e.message, e)
        }

        return count
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.IdGen
import uk.co.mingzilla.flatorm.util.InFn

import java.sql.*
import java.util.Date

@CompileStatic
class OrmWrite {

    static OrmErrorCollector validateAndSave(Connection conn, OrmDomain domain) {
        OrmErrorCollector errorCollector = domain.validate()
        if (!errorCollector.hasErrors()) {
            insertOrUpdate(conn, domain)
        }
        return errorCollector
    }

    static boolean delete(Connection conn, OrmDomain domain) {
        PreparedStatement statement = createDeletePreparedStatement(conn, domain)
        int rowsAffected = statement.executeUpdate()
        return rowsAffected > 0 // return true if row is deleted
    }

    static OrmDomain insertOrUpdate(Connection conn, OrmDomain domain) {
        boolean isNew = IdGen.isGeneratedId(domain.id)
        if (isNew) {
            PreparedStatement statement = createInsertPreparedStatement(conn, domain)
            int rowsAffected = statement.executeUpdate()
            if (rowsAffected > 0) {
                OrmMapping idMapping = OrmMapping.getIdMapping(domain.resolveMappings())
                domain.id = resolveId(statement.generatedKeys, idMapping)
            }
        } else {
            PreparedStatement updateStmt = createUpdatePreparedStatement(conn, domain)
            updateStmt.executeUpdate()
        }
        return domain
    }

    private static PreparedStatement createInsertPreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String sql = createInsertStatement(domain.tableName(), nonIdMappings)
        PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        statement = setStatementParams(statement, domain, nonIdMappings)
        return statement
    }

    private static String createInsertStatement(String tableName, List<OrmMapping> nonIdMappings) {
        String fieldNames = nonIdMappings*.dbFieldName.join(', ')
        String valuePlaceholders = nonIdMappings.collect { '?' }.join(', ')
        return """insert into ${tableName.toLowerCase()} (${fieldNames}) values (${valuePlaceholders})"""
    }

    private static PreparedStatement createUpdatePreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        OrmMapping idMapping = idAndNonIdMappings[0][0]
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]
        String sql = createUpdateStatement(domain.tableName(), domain.id, idMapping, nonIdMappings)
        PreparedStatement statement = conn.prepareStatement(sql)
        statement = setStatementParams(statement, domain, nonIdMappings)
        return statement
    }

    private static String createUpdateStatement(String tableName, Integer id, OrmMapping idMapping, List<OrmMapping> nonIdMappings) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        String setStatement = nonIdMappings.collect { "${it.dbFieldName} = ?" }.join(', ')
        return """update ${tableName.toLowerCase()} set ${setStatement} where ${idMapping.dbFieldName} = ${String.valueOf(id)}"""
    }

    private static PreparedStatement createDeletePreparedStatement(Connection conn, OrmDomain domain) {
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(domain.resolveMappings())
        OrmMapping idMapping = idAndNonIdMappings[0][0]
        String sql = createDeleteStatement(domain.tableName(), idMapping)
        PreparedStatement statement = conn.prepareStatement(sql)
        statement.setInt(1, domain.id);
        return statement
    }

    private static String createDeleteStatement(String tableName, OrmMapping idMapping) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        return """delete from ${tableName.toLowerCase()} where ${idMapping.dbFieldName} = ?"""
    }

    private static PreparedStatement setStatementParams(PreparedStatement statement, OrmDomain domain, List<OrmMapping> nonIdMappings) {
        nonIdMappings.eachWithIndex { OrmMapping it, Integer index ->
            Integer oneBasedPosition = index + 1
            Class type = InFn.getType(domain.class, it.camelFieldName)
            switch (type) {
                case boolean:
                case Boolean.class:
                    Boolean v = InFn.propAsBoolean(it.camelFieldName, domain)
                    statement.setBoolean(oneBasedPosition, v)
                    break
                case BigDecimal.class:
                    BigDecimal v = InFn.propAsBigDecimal(it.camelFieldName, domain)
                    statement.setBigDecimal(oneBasedPosition, v)
                    break
                case Date.class:
                    try {
                        Date d = InFn.prop(it.camelFieldName, domain) as Date
                        statement.setDate(oneBasedPosition, new java.sql.Date(d.time))
                    } catch (Exception ignore) {
                        // ignore invalid date
                    }
                    break
                case double:
                case Double.class:
                    Double v = InFn.propAsDouble(it.camelFieldName, domain)
                    statement.setDouble(oneBasedPosition, v)
                    break
                case float:
                case Float.class:
                    Float v = InFn.propAsFloat(it.camelFieldName, domain)
                    statement.setFloat(oneBasedPosition, v)
                    break
                case int:
                case Integer.class:
                    Integer v = InFn.propAsInteger(it.camelFieldName, domain)
                    statement.setInt(oneBasedPosition, v)
                    break
                case long:
                case Long.class:
                    Long v = InFn.propAsLong(it.camelFieldName, domain)
                    statement.setLong(oneBasedPosition, v)
                    break
                case String.class:
                    String v = InFn.propAsString(it.camelFieldName, domain)
                    statement.setString(oneBasedPosition, v)
                    break
                default:
                    break
            }
        }

        return statement
    }

    private static Integer resolveId(ResultSet generatedKeys, OrmMapping idMapping) {
        if (!idMapping) throw new UnsupportedOperationException('Missing OrmMapping for id')
        if (!generatedKeys.next()) return null // call next() to move the ResultSet cursor
        ResultSetMetaData metaData = generatedKeys.metaData
        int columnCount = metaData.columnCount
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i)
            if (idMapping.dbFieldName.equalsIgnoreCase(columnName)) {
                return generatedKeys.getInt(i)
            }
        }
        // it is possible that a driver is implemented to return 'insert_id' (rather than using the actual column name) as the columnName
        // If that happens, we fallback to use 1. Typically, the generated key is the first column in the ResultSet
        return generatedKeys.getInt(1)
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.util.ConnectionUtil

import java.sql.Connection

@CompileStatic
class OrmActor {

    static <T> T run(Connection connection, Closure<T> fn) {
        if (!connection) return null
        T result = null
        try {
            result = fn(connection)
        } catch (Exception ignore) {
        } finally {
            ConnectionUtil.close(connection)
        }
        return result
    }

    /**
     * Run in a transaction.
     */
    static <T> T runInTx(Connection connection, Closure<T> fn) {
        if (!connection) return null
        T result = null
        try {
            connection.setAutoCommit(false)
            result = fn(connection)
            connection.commit()
        } catch (Exception ignore) {
            connection.rollback()
        } finally {
            ConnectionUtil.close(connection)
        }
        return result
    }

    static void terminate() {
        throw new Exception('Terminate transaction and rollback')
    }
}

```


```groovy
package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.definition.AbstractOrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.definition.OrmValidate
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection
import java.sql.PreparedStatement

import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.minLength
import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.required

@CompileStatic
class MyPerson implements OrmDomain {

    Integer id
    String name

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('id', 'serial'),
                OrmMapping.create('name', 'usercode'),
        ])
    }

    @Override
    OrmErrorCollector validate() {
        OrmErrorCollector item = OrmErrorCollector.create(this)
        OrmValidate.with(item, 'id', [required()])
        OrmValidate.with(item, 'name', [required()])
        OrmValidate.ifSatisfies({ id == 1 }).then(item, 'name', [minLength(5)])
        return item
    }

    @Override
    String tableName() {
        return 'mis_users'
    }

    static List<MyPerson> listByNameStartsWith(Connection connection, String prefix) {
        String sql = """
        select * 
        from mis_users
        where usercode like ?
        """
        return OrmRead.list(connection, MyPerson.class, sql, { PreparedStatement it ->
            it.setString(1, "${prefix}%")
            return it
        })
    }
}

```


```groovy
package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.conn.ConnectionDetail
import uk.co.mingzilla.flatorm.util.ConnectionUtil

import java.sql.Connection

@CompileStatic
class RepoDb {

    static Connection getConn() {
        try {
            return createTargetDbConnection()
        } catch (Exception ex) {
            // Log error here. OrmActor expects a connection.
            throw new RuntimeException(ex.message, ex)
        }
    }

    private static Connection createTargetDbConnection() {
        ConnectionDetail detail = ConnectionDetail.create([
                "driverClassName": "org.mariadb.jdbc.Driver",
                "url"            : "jdbc:mariadb://localhost:3316/storage",
                "user"           : "root",
                "password"       : "test1234",
        ])
        return ConnectionUtil.getConnection(detail.driverClassName, detail.url, detail.connProperties)
    }
}

```


```groovy
package uk.co.mingzilla.example

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector
import uk.co.mingzilla.flatorm.util.IdGen

import java.sql.Connection

@CompileStatic
class MyApp {

    static void main(String[] args) {
        runWithoutTx()
        runWithTx()
    }

    static void runWithoutTx() {
        OrmActor.run(RepoDb.conn, { Connection conn ->
            println 'run'
            IdGen idGen = IdGen.create() // <-
            List<MyPerson> people1 = OrmRead.listAll(conn, MyPerson.class) // <- Example usage
            List<MyPerson> people2 = MyPerson.listByNameStartsWith(conn, 'An') // <-
            MyPerson person = OrmRead.getById(conn, MyPerson.class, 1) // <-

            println OrmRead.count(conn, MyPerson.class) // <-
            println people1*.name.join(', ')
            println people2*.name.join(', ')
            println person?.name

            MyPerson p = new MyPerson(id: idGen.int, name: 'Andrew')
            OrmErrorCollector collector = OrmWrite.validateAndSave(conn, p) // <-

            println p.id
            println collector.hasErrors() // <-
            println OrmRead.count(conn, MyPerson.class)

            boolean isDeleted = OrmWrite.delete(conn, p) // <-
            println isDeleted
            println OrmRead.count(conn, MyPerson.class)
        })
    }

    static void runWithTx() {
        Map errorMap = [:]
        OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            println 'runInTx'
            IdGen idGen = IdGen.create() // <-

            println OrmRead.count(conn, MyPerson.class)
            OrmErrorCollector collector1 = OrmWrite.validateAndSave(conn, new MyPerson(id: idGen.int, name: 'Bobby')) // <- success
            println OrmRead.count(conn, MyPerson.class)

            MyPerson p = new MyPerson(name: 'Christine')
            OrmErrorCollector collector2 = OrmWrite.validateAndSave(conn, p) // <- failure
            println OrmRead.count(conn, MyPerson.class)

            List<OrmErrorCollector> people = [collector1, collector2]
            boolean haveErrors = OrmErrorCollector.haveErrors([people])
            if (haveErrors) {
                errorMap = [people: OrmErrorCollector.toErrorMaps(people)]
                OrmActor.terminate() // <- trigger rollback, so that Bobby is not saved
            }
        })

        // when used in a controller, this can be returned as an API response
        println errorMap // [people:[[id:[[field:id, constraint:REQUIRED, invalidValue:null]]]]]
    }
}

```

And the below are unit tests to help you to create the desired python outcome

```groovy
package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

class MyPersonSpec extends Specification {

    def "Test creation"() {
        expect:
        new MyPerson() != null
    }

    void "Test validate"() {
        given:
        MyPerson person = new MyPerson(id: 1, name: 'Andy')

        when:
        OrmErrorCollector domainErrors = person.validate()

        then:
        assert domainErrors.hasErrors()
        assert domainErrors.toMap() == [
                'name': [
                        [constraint: 'MINIMUM_LENGTH', constraintValue: '5', field: 'name', invalidValue: 'Andy']
                ]
        ]
    }
}

```


```groovy
package uk.co.mingzilla.example

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.OrmActor
import uk.co.mingzilla.flatorm.domain.OrmRead

import java.sql.Connection

class RepoDbSpec extends Specification {

    void "Test run"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person
        long count = 0

        OrmActor.run(RepoDb.conn, { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listByNameStartsWith(connection, 'A') // custom sql
            person = OrmRead.getById(connection, MyPerson.class, 1)
            count = OrmRead.count(connection, MyPerson.class)
        })

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
        count > 0
    }

    void "Test runInTx"() {
        given:
        List<MyPerson> people1 = []
        List<MyPerson> people2 = []
        MyPerson person

        OrmActor.runInTx(RepoDb.conn, { Connection connection ->
            people1 = OrmRead.listAll(connection, MyPerson.class)
            people2 = MyPerson.listByNameStartsWith(connection, 'A') // custom sql
            person = OrmRead.getById(connection, MyPerson.class, 1)
        })

        expect:
        people1.size() > 0
        people2.size() > 0
        person != null
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.definition

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.example.MyPerson

import java.sql.ResultSet

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

    @Unroll
    def "test create method with camelFieldName: #camelFieldName and dbFieldName: #dbFieldName"() {
        when:
        OrmMapping ormMapping = OrmMapping.create(camelFieldName, dbFieldName)

        then:
        ormMapping.camelFieldName == camelFieldName
        ormMapping.dbFieldName == dbFieldName

        where:
        camelFieldName | dbFieldName
        "name"         | "NAME"
        "age"          | "AGE"
        "address"      | "ADDRESS"
    }

    def "test mapDomain with default mappings"() {
        given:
        List<OrmMapping> expectedMappings = [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        List<OrmMapping> mappings = OrmMapping.mapDomain(TestDomain.class)

        then:
        mappings.size() == expectedMappings.size()
        mappings*.camelFieldName.containsAll(expectedMappings*.camelFieldName)
        mappings*.dbFieldName.containsAll(expectedMappings*.dbFieldName)
    }

    def "test mapDomain with custom mappings"() {
        given:
        List<OrmMapping> customMappings = [OrmMapping.create("customField", "CUSTOM_FIELD")]
        List<OrmMapping> expectedMappings = customMappings + [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        List<OrmMapping> mappings = OrmMapping.mapDomain(TestDomain.class, customMappings)

        then:
        mappings.size() == expectedMappings.size()
        mappings*.camelFieldName.containsAll(expectedMappings*.camelFieldName)
        mappings*.dbFieldName.containsAll(expectedMappings*.dbFieldName)
    }

    def "test toDomain method"() {
        given:
        ResultSet resultSet = Mock(ResultSet)
        resultSet.getObject("NAME") >> "John"
        resultSet.getObject("AGE") >> 25
        resultSet.getObject("ACTIVE") >> true

        List<OrmMapping> mappings = [
                OrmMapping.create("name", "NAME"),
                OrmMapping.create("age", "AGE"),
                OrmMapping.create("active", "ACTIVE")
        ]

        when:
        TestDomain domain = OrmMapping.toDomain(mappings, resultSet, { props -> new TestDomain(props) })

        then:
        domain.name == "John"
        domain.age == 25
        domain.active
    }

    static class TestDomain {
        String name
        Integer age
        Boolean active

        TestDomain(Map<String, Object> props) {
            if (props) {
                this.name = props['name']
                this.age = props['age'] as int
                this.active = props['active'] as boolean
            }
        }
    }
}

```


```groovy
package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import static uk.co.mingzilla.flatorm.domain.validation.OrmConstraint.*

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

```


```groovy
package uk.co.mingzilla.flatorm.domain

import spock.lang.Specification
import uk.co.mingzilla.flatorm.domain.definition.OrmDomain
import uk.co.mingzilla.flatorm.domain.definition.OrmMapping
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.PreparedStatement

class OrmWriteSpec extends Specification {

    private class MyPerson implements OrmDomain {
        boolean booleanField
        Boolean boolean2Field

        BigDecimal bigDecimalField

        Date dateField

        double doubleField
        Double double2Field

        float floatField
        Float float2Field

        int idField
        Integer id

        long longField
        Long long2Field

        String name

        @Override
        List<OrmMapping> resolveMappings() {
            return OrmMapping.mapDomain(MyPerson.class, [])
        }

        @Override
        OrmErrorCollector validate() {
            return null
        }

        @Override
        String tableName() {
            return 'people'
        }
    }

    def "Test setStatementParams method"() {
        given:
        OrmDomain person = new MyPerson(
                id: 1,
                booleanField: true,
                boolean2Field: false,
                bigDecimalField: 100,
                dateField: new Date(),
                doubleField: 2.20,
                double2Field: 4.20,
                floatField: 1.20,
                float2Field: 3.20,
                idField: 5,
                longField: 11L,
                long2Field: 12L,
                name: 'John',
        )
        List<List<OrmMapping>> idAndNonIdMappings = OrmMapping.splitIdAndNonIdMappings(person.resolveMappings())
        List<OrmMapping> nonIdMappings = idAndNonIdMappings[1]

        // Mock PreparedStatement
        PreparedStatement statement = Mock(PreparedStatement)

        when:
        OrmWrite.setStatementParams(statement, person, nonIdMappings)

        then:
        1 * statement.setBigDecimal(1, person.bigDecimalField)
        1 * statement.setBoolean(2, person.boolean2Field)
        1 * statement.setBoolean(3, person.booleanField)
        1 * statement.setDate(4, person.dateField)
        1 * statement.setDouble(5, person.double2Field)
        1 * statement.setDouble(6, person.doubleField)
        1 * statement.setFloat(7, person.float2Field)
        1 * statement.setFloat(8, person.floatField)
        1 * statement.setInt(9, person.idField)
        1 * statement.setLong(10, person.long2Field)
        1 * statement.setLong(11, person.longField)
        1 * statement.setString(12, person.name)
    }

    def "Test createInsertStatement method"() {
        given:
        String tableName = "MY_TABLE"
        List<OrmMapping> nonIdMappings = [
                new OrmMapping(camelFieldName: "name", dbFieldName: "Name"),
                new OrmMapping(camelFieldName: "age", dbFieldName: "Age")
        ]

        when:
        String insertStatement = OrmWrite.createInsertStatement(tableName, nonIdMappings)

        then:
        insertStatement == "insert into my_table (Name, Age) values (?, ?)"
    }

    def "Test createUpdateStatement method"() {
        given:
        String tableName = "MY_TABLE"
        Integer id = 1
        OrmMapping idMapping = new OrmMapping(camelFieldName: "id", dbFieldName: "ID")
        List<OrmMapping> nonIdMappings = [
                new OrmMapping(camelFieldName: "name", dbFieldName: "Name"),
                new OrmMapping(camelFieldName: "age", dbFieldName: "Age")
        ]

        when:
        String updateStatement = OrmWrite.createUpdateStatement(tableName, id, idMapping, nonIdMappings)

        then:
        updateStatement == "update my_table set Name = ?, Age = ? where ID = 1"
    }

    def "Test createDeleteStatement method"() {
        given:
        String tableName = "MY_TABLE"
        OrmMapping idMapping = new OrmMapping(camelFieldName: "id", dbFieldName: "ID")

        when:
        String updateStatement = OrmWrite.createDeleteStatement(tableName, idMapping)

        then:
        updateStatement == "delete from my_table where ID = ?"
    }
}

```
