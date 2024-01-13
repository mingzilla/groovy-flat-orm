package uk.co.mingzilla.flatorm.domain.validation

/**
 * Example:
 * <pre>
 *
 * minLength: [
 *   rule: 'minLength',
 *   value: '5',
 *   invalidFields: [
 *     [ field: 'name', value: 'hi' ],
 *     [ field: 'identifier', value: 'hi' ],
 *   ]
 * ]
 * </pre>
 * @author ming.huang
 * @since 13/01/2024
 */
class DomainError {
    String rule
    String value
    List<Map<String, Object>> invalidFields
}
