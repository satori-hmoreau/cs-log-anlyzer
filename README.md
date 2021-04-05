# cs-log-anlyzer - A sort of a log analyzer application 
This is a spring boot application that implements the spec to be found here in the *./spec* directory.
It follows the spec quite closely and varies only to the extent that it accepts multiple filenames on the command line and processes each one in turn. 
## Build it
To build the application, you must have a java 11 development system installed on your platform.
Java 11.0.1 was used for development, but any Java 11 version should work OK.

### Steps to build
1. Clone this repository.
2. Change directory to the **cs-log-anlyzer** directory.
3. Build it by running the maven wrapper provided for your OS.
  On Windows:
    
    ./mvnw.cmd clean install
    
 On Linux or MacOS:
 
     ./mvnw clean install
     
 The first build will take a while as it downloads a lot of dependent jar files.  The build process runs a suite of tests to ensure the 
 application does what is expected.
 
 The tests cover over 90% of the code of the application.  After the build you can see the details of that coverage in the jacoco report at *./target/site/jacoco/index.html*
 Jacoco has been configured so that a build fails if there isn't at least 80% test coverage by instructions.

As well as installing the output jar file in your maven repository, the jar file is created in the target directory: *./target/CS-LogAnalyzer-1.0.0-SNAPSHOT.jar*

## Run it
To run the application you invoke it using the java binary and providing the files you want to process as command line parameters after the jar file name.  There are a few small sample files in the *src/test/resources* directory.

    java -jar target/CS-LogAnalyzer-1.0.0-SNAPSHOT.jar src/test/resources/logfile.txt
    
By default the output of the program is stored in a HSQLDB file in the current directory.  The default name of the database is *csla_db*.
You can change where the database is put and its name on the command line by defining csla_db, e.g. to put the database in *D:/tmp/myhsqldb*
your command line would be

     java -jar -Dcsla_db=D:/tmp/myhsqldb target/CS-LogAnalyzer-1.0.0-SNAPSHOT.jar src/test/resources/logfile.txt
     
## Check it

To view the output you need to run the hsqldb UI.
It has been provided here, as is, in the lib directory.
You can run it with

    java -cp lib/hsqldb-2.5.1.jar org.hsqldb.util.DatabaseManagerSwing
    
You will have to edit the database URL in the dialog box that pops up.  For the database mentioned above in *D:/tmp/myhsqldb* the URL should read **jdbc:hsqldb:file:D:/tmp/myhsqldb**.

To view the records saved, execute the following SQL statement:

    SELECT * FROM MATCHED_EVENTS
    
The program is completely naive.  If you run it several times with the same file, it adds the records to the database on every run.
It make no attempt to batch or check for events already processed.

Do **not** keep the HSQLDB UI open at the same time as trying to run CS-LogAnalyzer.  HSQLDB uses process locking and one or both applications will hang if you try to access the database twice simultaneously.

## Architecture and development

The choice of spring boot may seem like overkill for a simple naive application like this.  It was chosen for several reasons:

* Spring boot provides a flexible and extensible architecture design pattern.
* It's easy for someone perusing the code to orient themselves as all components, logic and tests are in familiar places.
* You get a lot of functionality "free out of the box", e.g. configurable database connectivity, logging, sophisticated testing etc.
* If you want to modify the application to make it less naive and more actually useful, you're already well on your way without massive refactoring.



