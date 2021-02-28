## Overview
The program reads a JSON file storing log entries of different applications from different hosts, it matches two entries that compose events (entries labelled as "STARTED" and "FINISHED"). The events are flagged (alert = true) if the duration between two entries exceeded 4ms. All the events are recorded in the EVENT table of a HSQLDB database, which is stored in a local folder.

## Usage

The program is a standalone JAR package which requires Java 8 JRE installed to run. The user with which the program is run needs to have a read/write access to the local folder from which the program is run.

If the JSON file *logfile.txt* with entries is in the same folder and you're ok with the default logging setting it is enough to run a jar without any aguments:

> java -jar LogMonitor.jar

If the JSON file is in a different folder or has different name pass the path to the file as an argument when runnning the prgram:

> java -jar LogMonitor.jar "../logfile.txt"

If additionally you would like the program to use external settings for logging (by default the internal settings logging level is set to DEBUG), you can provide as parameter a path to the file with *log4j2* settings:

> java -Dlog4j.configurationFile=log4j2.xml -jar LogMonitor.jar "../logfile.txt"

## Build and test

The project is set up for a Maven build and test. Its build details and dependencies you will find in the *pom.xml*. The program uses different open source frameworks to execute.
* gson ver 2.8.6
* hsqldb ver 2.5.1
* log4j ver 2.14.0

Running Maven with Install goal over the project will run a unit test and produce a runnable JAR file.

## Extra features

The program has addressed the requested features in a following way:

### Proper use of Info and Debug logging
 
The program uses Apache *log4j2* framework to log different events during the execution. It outputs the logs both - to the console and to the file in the */logs* folder.

### Proper use of Object Oriented Programming

The program is split into classes and each entry through the parsing of the file is reflected as an object that populates a *List*. While doing this the corresponding entries (with th same ID) are looked for, and when the pair (one entry with status "STARTED" and one with status "FINISHED") is found it is converted to an event object and added to another *List* of Event type object.

### Unit test coverage

The program runs only one unit test. It uses *junit* framework for that. It is to test successful recording of the Events to the database. The test uses in-memory database for that. At the end of the test the query of the database is performed to see if the Events have been successfully recorded.

### Multi-threaded solution

The program runs two threads concurrently. First thread parses the file and serialises the entries into the objects. The second thread runs through the list of Events and populates the database with them. The shared resource in this scenario is the List of events. The program uses *java.util.Collections.synchronizedList* wrapper to make sure there are no concurrency issues during adding, removing and iterating operations over it. This has not been tested under circumstances with the two threads actually running concurrently, due to a small size of a test json file with only 6 entries.

### Program that can handle very large files (gigabytes)

To handle large files and avoid running into *OutOfMemoryError* the program uses *java.io.InputStream* to read the file, effectively removing the requirement to read the entire file into memory. During the execution of the program we remove the processed objects from the lists, allowing Garbage Collector to free up memory once the objects are no longer needed.  

## Step-by-step operations

The **LogMonitor** performs its task following the below algorithm:

**Thread 1**
1. Read a file as a stream of data      
2. Serialise JSON into Entry objects and add the to the list
3. Each time the entry is added it looks for a sibling entry with the same ID
4. If a pair is found it tries to create an Event object and add it to the event List
5. Upon successful event creation, the entries are removed from the list for perfomance reasons

Shortly after Thread 1 is started Thread 2 starts.

**Thread 2**
1. Open connection to the database
2. Create a copy of the list, to remove the lock on the events list.
3. Remove the cloned events from the shared list.
4. For each item on the cloned events list execute an Insert query on the database
5. When finished check if Thread 1 is still running. If it does, go back to point 2 and start again.
6. If Thread 1 is no longer running, close the connection with the database

## Successfull program output example

```
11:48:10.653 [Thread-0] INFO  main.Main - Beginning to read log file at logfile.txt
11:48:10.693 [Thread-0] DEBUG event.Event - Created Event with id: scsmbstgra
11:48:10.693 [Thread-0] DEBUG event.Event - Created Event with id: scsmbstgrc
11:48:10.693 [Thread-0] DEBUG event.Event - Created Event with id: scsmbstgrb
11:48:10.693 [Thread-0] INFO  main.Main - Finished parsing a log file. Left 0 entries unmatched.
11:48:10.845 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - Checkpoint start
11:48:10.846 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - checkpointClose start
11:48:10.847 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - checkpointClose synched
11:48:10.851 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - checkpointClose script done
11:48:10.867 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - checkpointClose end
11:48:10.868 [Thread-1] INFO  hsqldb.db.HSQLDB77E877A3CB.ENGINE - Checkpoint end - txts: 1
11:48:10.875 [Thread-1] DEBUG jdbcutils.JDBCUtils - Creating table in the database if not existent.
11:48:10.877 [Thread-1] INFO  main.Main - Beginning to add Events to the database
11:48:10.879 [Thread-1] DEBUG jdbcutils.JDBCUtils - Succesfully recorded event 'scsmbstgra' in the database
11:48:10.879 [Thread-1] DEBUG jdbcutils.JDBCUtils - Succesfully recorded event 'scsmbstgrc' in the database
11:48:10.879 [Thread-1] DEBUG jdbcutils.JDBCUtils - Succesfully recorded event 'scsmbstgrb' in the database
11:48:10.942 [Thread-1] INFO  main.Main - Finished adding Events to the database
```
