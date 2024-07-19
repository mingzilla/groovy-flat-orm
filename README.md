# Flat ORM

## Summary
This project generates a jar file to be used to query or update database using SQL. It includes 3 elements:
* ORM mapping
* Read data: 1) return one item or null, 2) return a list
* Write data: 1) direct, 2) transactional
Always manually close connection at the end of a request.

### Background
I'm exasperated about Hibernate. Especially when used in Grails projects.
* if an object is modified, it can be marked dirty and it updates the db record automatically
* saving sometimes needs flushing and sometimes doesn't, sometimes generates errors
* upgrading can result in behaviour changes to break your system
* too many ways to handle transactions
ORM is simple, so it's really not necessary to be too clever about it.

## Usage
* Define an Entity/Repository - `extends AbstractOrmDomain`
* Execute Read - `OrmActor.run`
* Execute Create / Update / Delete - `OrmActor.runInTx` to write

### Define an Entity/Repository
* refer to `MyPerson.groovy`. Here's a simple version. It extends from `AbstractOrmDomain` to get methods for free

```groovy
class MyPerson extends AbstractOrmDomain {

    Integer id
    String name

    @Override
    List<OrmMapping> resolveMappings() {
        return OrmMapping.mapDomain(MyPerson.class, [
                OrmMapping.create('name', 'usercode'), // custom mapping
        ])
    }

    @Override
    OrmErrorCollector validate() {
        OrmErrorCollector item = OrmErrorCollector.create(this)
        OrmValidate.with(item, 'id', [required()])
        OrmValidate.ifSatisfies({ id == 1 }).then(item, 'name', [minLength(5)])
        return item
    }

    @Override
    String tableName() {
        return 'mis_users'
    }
}
```

### Execute Read
* refer to `MyPersonController.groovy`

```groovy
class MyPersonController {

    Map get(Integer id) {
        Map resp = OrmActor.run(RepoDb.conn, { Connection conn ->
            MyPerson person = OrmRead.getById(conn, MyPerson.class, id)
            return InFn.toMap(person)
        })
        return resp
    }
}
```

### Execute Write
* refer to `MyPersonController.groovy` for Create / Update / Delete.

```groovy
class MyPersonController {

    Map put(Integer id, Map params) {
        Map resp = OrmActor.runInTx(RepoDb.conn, { Connection conn ->
            MyPerson item = OrmRead.getById(conn, MyPerson.class, id)
            MyPerson person = DomainUtil.mergeRequestData(item, params, params)
            OrmErrorCollector collector = OrmWrite.validateAndSave(conn, p)

            if (collector.hasErrors()) {
                return collector.toMap()
            } else {
                return InFn.toMap(person)
            }
        })
        return resp
    }
}
```

## More Examples
Check the `example` package.
* `MyApp` - an example of making a request
* `MyPersonController` - example usage in REST
* `Db1Actor` - provides a jndi connection to be used by a request 
* `MyPerson` - a domain class with mapping defined, check unit tests for custom mapping

## Commands for this project
* `./gradlew test`
* `./gradlew build`
* `./gradlew clean`
