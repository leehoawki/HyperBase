# HyperBase

A zero configuration key-value store that runs in a servlet container using Bitcask as the storage model.

![Datafiles](http://pic.yupoo.com/iammutex/BwqvS7Fs/wlJ3W.jpg)

Updates will get handled by service module and append into the datafiles directly. Meanwhile, the index of data in the memory will get updated too. It will create a new active data file when the old one becomes big enough. Older data files will get merged and archived regularly.

Queries will check the index in the memory first and then visit the position of the data files on disk to get the lastest data. After crash or shutdown, it will restore itself and try to recreate the index in memory using all the data files on disk.

## Building and Running
To build this project, you must have Maven and Jdk(1.8) installed.

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


