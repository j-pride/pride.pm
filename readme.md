# PriDE 3.0.1, 2018-04-27

(c) The PriDE Open Source Team

See license.txt for license conditions!
See doc/PriDE-History.html for release notes!

## Travis CI Status
[![Build Status](https://travis-ci.org/j-pride/pride.pm.svg?branch=maven)](https://travis-ci.org/j-pride/pride.pm)


## Building PriDE

PriDE is a maven based project. The JUnit-Tests can be executed against different types of databases.
At the moment the following types of databases are supported and actively tested on [Travis CI](https://travis-ci.org/j-pride/pride.pm):

* HSQLDB (Version 2.x.x)
* MySQL (Version 5.5)
* Postgres (Version 10.x)
* Oracle XE 11g
* Oracle EE (Exadata) **Not on Travis CI, but works in production!**

To build PriDE _and_ execute all tests, you have to specify a database to run the tests on:

    mvn package -Duser.name=[mysql,hsql,oracle,postgres]

To just build PriDE without execute any tests run
    
    mvn package -DskipTests
    
Afterwards the built jar can be found in `target/pride-<Major-Version>-SNAPSHOT-<Git-Commit-ID>.jar`

### Preperations needed for Oracle

If you plan to use an Oracle database, you have to manually install the JDBC driver into your local maven repository.
Download the ojdbc8.jar from the [Oracle Technology Network](http://www.oracle.com/technetwork/database/features/jdbc/jdbc-ucp-122-3110062.html).
Afterwards you can install it into your local repository by executing the following command

	mvn install:install-file -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar -Dfile=ojdbc8.jar -DgeneratePom=true