






--- 

Been built into a micronaut project on micronaut:
```
 mn --version
| Micronaut Version: 1.0.0.M4
| JVM Version: 1.8.0_171
```

Please ensure you have MongoDB installed locally
----

Please ensure you are also running kafka 
----
```
-> sudo /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties

-> sudo /opt/kafka/bin/kafka-topics.sh --list --zookeeper localhost:2181
```

Please ensure you are running [zipkin](https://github.com/openzipkin/zipkin)
---
```
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
# or
docker run -d -p 9411:9411 openzipkin/zipkin

```



1. Run Consul

To run first either install consul locally and run `./consul agent dev` 

or if you have installed docker simply run `sudo docker run -p 8500:8500 consul`


2. To run 1 instance of thes beer billing / waiter / stock instances run this from within this project folder:

```
cd micronaut-ms

./gradlew beer-stock:run beer-billing:run  beer-waiter:run  beer-front:run --parallel

```

```
./gradlew frontend:react:start
```

3. You can launch any of these instances as many times as machine can handle
```
./gradlew beer-waiter:run  beer-billing:run beer-stock:run --parallel
```




Youtube
----
[Youtube full walkthrough of the beer solution covering full fall back](https://youtu.be/nkDdlu1cJEw)

> To launch the site call http://localhost:3000 on the browser

> To view consul goto http://localhost:8500

> To view tracing via zipkin goto http://localhost:9411



Event sourcing microservices
----

During research and talking with fellow work colleagues, it became aparent that to have a resilient microservice 
architecture in place event sourcing is an important part of ensuring if things were to go down you would have a way of replaying
transactions back in sequence sent.

In this demo project there are 2 kafka applications `beer-billing` which listens to kafka events and `beer-tab` 
which is a very primitive micro service application, it simply mimicks a beer sale which it publishes as an event to `kafka`

When `beer-billing` comes alive the kafka stream awaiting is replayed and upon boot those transactions are added back in to mongo db.


In this example there is 1 instance of `mongodb` it would probably be safe to have 2 mongo clusters 1 for `billing` 
which all instances of `billing` that start point to and another mongo cluster for the `beer-stock` application. 
which all instances of stock point to the this cluster.

Useful links:

https://ghost.kontena.io/event-sourcing-microservices-with-kafka/

https://medium.com/lcom-techblog/scalable-microservices-with-event-sourcing-and-redis-6aa245574db0

https://dzone.com/articles/event-driven-microservices-patterns

https://blog.couchbase.com/event-sourcing-event-logging-an-essential-microservice-pattern/  



Mongo DB Notes
--------

Removing DBs CLI, this is on Linux using mongo client:
```
mongo
MongoDB shell version v3.4.7
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.4.7
Server has startup warnings: 
> show dbs;
admin                 0.000GB
beerbilling           0.000GB
beerstock             0.000GB
billing               0.000GB
local                 0.000GB
petclinic             0.000GB
sett-todolist-master  0.000GB
> use beerstock
switched to db beerstock
> db.dropDatabase();
{ "dropped" : "beerstock", "ok" : 1 }

```

The commands are `mongo` followed by `use {dbName};` then `db.dropDatabase()`

I had to do this to beerStock due to how it was done - recreate test etc 
