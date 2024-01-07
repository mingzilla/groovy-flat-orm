## Create this project
### Generate Project
* gradle init
* choose: groovy, library, flat-orm

### Add Idea Support
Under root directory, add build.gradle with

```groovy
plugins {
  id 'idea'
}

idea {
  module {
    // Exclude the 'gradle' directory from being considered as a source directory
    excludeDirs += file('build-logic')
    excludeDirs += file('gradle')
  }
}
```

### Dependencies
Edit lib/build.gradle dependencies

### Rename built jar file
Edit lib/build.gradle, add

```groovy
archivesBaseName = 'flatorm'
```

### Test creation
* Make the unit test failed
  * run the test in intelliJ
  * run the test in command line `./gradlew test`
  * run the test in command line `./gradlew build`
* Change the test to pass, and run the above again