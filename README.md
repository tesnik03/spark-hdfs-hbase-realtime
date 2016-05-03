# spark-hdfs-hbase-realtime
Realtime schema updates through spark, hbase, hdfs

This project is for realtime schema update. The ideas behind this project are following -

1. To keep the event history of all the updates that happened over the period of time in hdfs
2. Although tables might contain entire history of changes, we need to provide most recent data from the tables that can be used in BI tools for reporting purposes
3. Consume streaming messages coming through kafka and use it in BI tools

#Prerequisites
1. Having a HBase set up ready
2. Having a HDFS setup ready
3. Kafka Queue should be available
5. Messages should have a unique identifier, like primary key in the database
6. Message should have the version number. Little explanation about this -
     
     Lets say we receive data as 
     
     ```{"pkCol":1, "name":"John", "age":"24", "version": 1}``` event 1
	   If we want to change name John to Mary, We will send another event like 
	   
	   ````{"pkCol":1, "name":"Mary", "age":"24", "version": 2}```` event 2
	   
	   You can notice that even if we are not changing age of John, we are still sending it in new version of json. Also, you can see that the version is chagned.
7. The code to parse JSON messages and put it in the right format will be application specific and will be done by the user. 
8. We will use Impala to store full data.
9. Impala requires to run "Refresh <table name>" whenever you load any additional data file. 



#Concept behind this project -
1. HBase writes are costly, so we will have only primary key and version per table in HBase. For a given JSON example - 
    ````{"pkCol":1, "name":"Mary", "age":"24", "version": 2}````
   Lets say, primary key is pKCol, then in that case we will have pkCol and version as the column and have it in the HBASE.
2. To get the latest data for a given table, we will create a view on top Impala table and HBase table.
   
   Example - create view Person as ````Select * from Person_Impala pi join Person_Hbase ph where pi.pkCol = ph.pKCol and pi.version = ph.version```` 
   Since, Person_Hbase will always have most recently update version and pkCol can provide uniqueness, you wiill always get the latest data.

#Example case -

We have a requirement where we have to keep track of all changes that happens over the period of time in data set. But for the BI tools we have to provide the latest data.

To simplify things, lets take an example of 1 row. This is just an example but our tables have more that 20 columns each.  Lets say we keep getting json like 

````{"pkCol":1, "name":"John", "age":"24", "version": 1}```` at time T1

````{"pkCol":1, "name":"Mary", "age":"24", "version": 2}```` at time T2

We need to keep both these rows in Impala person_impala table like 
````
    1|John|24|1 
    
    1|Mary|24|2 
````

But for the BI tools only 1|Mary|24|2 , should be provided. 

HBase provides way to update database but writes are the problem. Also, it will very costly and time consuming to write all 20 columns. To achieve this, have create another table in HBase with just primary key column and version like for the above dataset, its going to look like 

````
1|2
````

which means, for id 1, latest record is version 2. Now we can create a view by saying 

create view as person as select * from person_impala pi join person_hbase ph on pi.id = ph.id and pi.version = ph.version

With this, as long as we will be using view instead of table person_impala, we will always get the latest record. 


To use this, you need to do the following -

1. Create table on top of HDFS, that will be used by Impala

2. Create HBase table with minimum parameter

3. Implement your own way to parse the incoming json [or whatever format data is coming ]

![Alt text](/Functionality.jpg?raw=true "Dataflow")
