# Requirement #

To create a web application programming interface, the service shall provide clients with add, remove and view functions for watch lists contents.
JSON protocol shall be the input and output for this application. Users/clients can access this API using various interfaces such as mobile, web, etc.,

# Design Decisions and Assumptions #

Following design decisions and assumptions are made while developing the application.

1. Input has been designed to accept multiple customer's watchlist assuming the API can be used by other upstream application or directly from customer's console
2. Customer Id and Content Id are only validated only for the size and being alphanumeric, it is assumed to follow strict requirements on the size of the id's, i.e size should only be 3 and 5
3. To avoid race conditions customer id is locked during add and delete
4. This design caters for partial update of the content id's, i.e, when a valid and invalid content are sent for add or delete the valid id is applied and invalid is reported back to the caller. 
 
# System Requirements #

This application was developed using IntelliJ IDEA targeted to run in a Java 8 VM, following are the libraries and binaries used
 
JDK 1.8.0_181

SBT 1.1.6

Scala 2.12

Play 2.6

Cats 1.1.0

Scoverage Plugin for code coverage. Run `sbt clean coverage test coverageReport` for coverage report  

# Building & running application #
This application can be built by running `sbt dist `. This operation shall create deployable contents in target directory.

Application can then be run by executing `target/universal/scripts/nowtvwatchlist` 

You can also run the application in dev mode `sbt run`

