# Flat ORM

## Summary
This project generates a jar file to be used to query or update database using SQL. It includes 3 elements:
* ORM mapping
* Read data: 1) return one item or null, 2) return a list
* Write data: 1) direct, 2) transactional

### Background
I'm exasperated about Hibernate. Especially when used in Grails projects.
* if an object is modified, it can be marked dirty and it updates the db record automatically
* saving sometimes needs flushing and sometimes doesn't, sometimes generates errors
* upgrading can result in behaviour changes to break your system
* too many ways to handle transactions
ORM is simple, so it's really not necessary to be too clever about it.

## Commands for this project
* `./gradlew test`
* `./gradlew build`
* `./gradlew clean`
