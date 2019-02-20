# PriDE 3.4.2 2019-02-20

(c) The PriDE Open Source Team

See license.txt for license conditions according to Apache Licence 2!
See doc/PriDE-History.html for release notes!

## Latest Release on Maven Central
[![Maven Central](https://img.shields.io/maven-central/v/pm.pride/pride.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22pm.pride%22%20AND%20a:%22pride%22)

## Travis CI Status
[![Build Status](https://travis-ci.org/j-pride/pride.pm.svg)](https://travis-ci.org/j-pride/pride.pm)


## Building PriDE

PriDE is a Maven-based project. The JUnit-Tests can be executed against different types of databases.
At the moment the following types of databases are supported and actively tested on [Travis CI](https://travis-ci.org/j-pride/pride.pm):

* HSQLDB (Version 2.x)
* SQLite (Version 3.x)
* MySQL (Version 5.5)
* MariaDB (Version 10.3)
* Postgres (Version 9.6)
* Oracle XE 11g
* Oracle EE (Exadata) **Not on Travis CI, but works in production!**

To build PriDE _and_ execute all tests, you have to specify a database to run the tests on:

    mvn package -Duser.name=[mysql,hsql,mariadb,oracle,postgres,sqlite]

To just build PriDE without execute any tests run
​    
    mvn package -DskipTests

Afterwards the built jar can be found in `target/pride-<Major-Version>-SNAPSHOT-<Git-Commit-ID>.jar`

### Preperations needed for Oracle

If you plan to use an Oracle database, you have to manually install the JDBC driver into your local maven repository.
Download the ojdbc8.jar from the [Oracle Technology Network](http://www.oracle.com/technetwork/database/features/jdbc/jdbc-ucp-122-3110062.html).
Afterwards you can install it into your local repository by executing the following command

	mvn install:install-file -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar -Dfile=ojdbc8.jar -DgeneratePom=true
