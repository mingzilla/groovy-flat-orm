package uk.co.mingzilla.flatorm.domain.definition

import groovy.transform.CompileStatic
import uk.co.mingzilla.flatorm.domain.OrmRead
import uk.co.mingzilla.flatorm.domain.OrmWrite
import uk.co.mingzilla.flatorm.domain.validation.OrmErrorCollector

import java.sql.Connection

/**
 * Shows all the available ORM default functions provided by this library.
 * Extend this class to get these methods for free.
 * However they are all just oneliners so just use {@link OrmRead} and {@link OrmWrite} directly in real implementation.
 *
 * @since 09/06/2024
 * @author ming.huang
 */
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
