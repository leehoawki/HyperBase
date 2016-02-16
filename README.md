# HyperBase
A zero configuration, non-distributed key-value store that runs in a servlet container.


## Building and Running

To build this project, you must have Maven installed.

    git clone https://github.com/leehoawki/HyperBase.git
    cd HyperBase
    mvn package 

Copy the war into Tomcat or any other servlet container you like and start it. Or just using mvn jetty plugin to launch it.

    mvn jetty:run

Visit <a href="http://localhost:8080/HyperBase">http://localhost:8080/HyperBase</a> and you should see the Admin page. 

## API

HyperBase provides a RESTful API that allows to query data using GET and update/delete data using POST in the repositories. 

##### List repositories
    GET:/api/repositories

##### Query a repository details
    GET:/api/[repository]

##### List tables in a repository
    GET:/api/[repository]/tables

##### Query a table details
    GET:/api/[repository]/[table]

##### Query data in a table by a key
    GET:/api/[repository]/[table]/[key]

##### Create/Delete a repository
    POST:/api/[repository]
    data:action = create/delete

##### Create/Delete a table
    POST:/api/[repository]/[table]
    data:action = create/delete

##### Update/Delete data in a table
    POST:/api/[repository]/[table]/[key]
    data:action = update/delete
         val    = [val]



## TODO





