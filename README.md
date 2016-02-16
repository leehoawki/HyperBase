# HyperBase
A zero configuration, non-distributed key-value store that runs in a servlet container.

    ++++++++++++++++++++++++++++++++++++
    |                                  |
    |     Controller                   |
    |         |                        |
    |         V                        |
    |      Service  ----->  Writer     |
    |         |               |        |
    |         V               V        |
    |      RedoLog           File      |
    |                                  |
    ++++++++++++++++++++++++++++++++++++
    
    Admin: An admin page where you can get some description/monitoring data and create/delete objects as you need.

Queries/Updates will get handled by service module and write all updates info into redo log. Meanwhile, modified entities in memory will be sent to another writer thread to dump into a data file on disk. Then it can restore itself after crash or shutdown using the snapshot on disk and the redo log.

## Building and Running

To build this project, you must have Maven installed.

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
    data:action = create/delete

##### Update/Delete data in a table
    POST:/api/[table]/[key]
    data:action = update/delete
         val    = [val]



## TODO





