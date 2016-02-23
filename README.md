# HyperBase


![UNDER CONSTRUCTION](http://ocp.sparwood.ca/wp-content/uploads/2013/12/Under-construction.jpg)

## Overview

A zero configuration key-value store that runs in a servlet container. Snapshot and instance restore function supported.

    ++++++++++++++++++++++++++++++++++++
    |                                  |
    |     Controller                   |
    |         |                        |
    |         V                        |
    |      Service   --->   DBWR       |
    |         |               |        |
    |         V               V        |
    |       LGWR           DataFile    |
    |         |                        |
    |         V                        |
    |      RedoLog                     |
    |                                  |
    ++++++++++++++++++++++++++++++++++++
    
   
Queries/Updates will get handled by service module and write all updates info into redo log. Meanwhile, modified entities in memory will be sent to another writer thread to dump into data files on disk. So it can restore itself after crash or shutdown using the snapshot and the redo log on disk.

## Building and Running

To build this project, you must have Maven and Java8 installed.

    git clone https://github.com/leehoawki/HyperBase.git
    cd HyperBase
    mvn package 

Copy the war into Tomcat or any other servlet container you like and start it. Or just using mvn jetty plugin to launch it.

    mvn jetty:run

Visit [http://localhost:8080/HyperBase](http://localhost:8080/HyperBase) and you should see the Admin page. 

## API

HyperBase provides a RESTful API that allows to query data using GET and update/delete data using POST in the repositories. 

##### List tables
    GET:/api/tables

##### Query a table details
    GET:/api/[table]

##### Query data in a table by a key
    GET:/api/[table]/[key]

##### Create/Delete a table
    POST:/api/[table]
    Content-Type:application/json
    data = create/delete

##### Update data in a table
    POST:/api/[table]/[key]
    Content-Type:application/json
    data = [val]

Data will get removed after being updated as an empty string.






