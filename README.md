Forked from: https://github.com/mfarache/micronaut-ms. The project did not appear to work for me so I have made it from scratch taking the content of the other project:


Please note under development - Below instructions will currently not work
--- 

Been built into a micronaut project on micronaut:
```
 mn --version
| Micronaut Version: 1.0.0.M4
| JVM Version: 1.8.0_171
```

1. Run Consul

To run first either install consul locally and run `./consul agent dev` 

or if you have installed docker simply run `sudo docker run -p 8500:8500 consul`


2. To run 1 instance of thes beer billing / waiter / stock instances run this from within this project folder:

```
cd micronaut-ms

./gradlew beer-stock:run beer-billing:run  beer-waiter:run  --parallel

```

```
./gradlew frontend:react:start
```

3. Then launch separate instances again
```
./gradlew beer-waiter:run  beer-billing:run beer-stock --parallel
```


4. Then launch separate instances again (This now making 3 instances of billing / waiter / stock all running )
```
./gradlew beer-waiter:run  beer-billing:run beer-stock --parallel
```





Now open browser hit:

http://localhost:8082/waiter/beer/fred `{"name":"mahou","size":"MEDIUM"}`

Run again http://localhost:8082/waiter/beer/fred `{"name":"mahou","size":"MEDIUM"}`

Bill fred:
http://localhost:8082/waiter/bill/fred `{"cost":23.4,"deskId":8081}`

Start with Wilma

http://localhost:8082/waiter/beer/wilma `{"name":"mahou","size":"MEDIUM"}`

http://localhost:8082/waiter/bill/wilma `{"cost":11.7,"deskId":8081}`



Then run the `test.sh` script  watch all 3 billing systems 

Videos
----

I have uploaded a video on you tube based on this branch:

1. [YouTube: latest video - replaces existing](https://www.youtube.com/watch?v=zN9OyTBiG7s)

2.[YouTube: Part 2 simplied version](https://www.youtube.com/watch?v=TYCDUDsILVQ) 

Description: Triangulated websockets
-----
[beer-websocket: LocalSocket.java](https://github.com/vahidhedayati/micronaut-playground/blob/beerwebsocket-demo/micronaut-ms/beer-websocket/src/main/java/beer/websocket/LocalWebSocket.java)
This is referred to as websocket connection tab `/ls/` stands for local socket. This is used by beer-websocket application.
It is triggered by 
[TransactionWebsocket.java: bootService.shareSessions();](https://github.com/vahidhedayati/micronaut-playground/blob/dd13f9af63b764482d248c70e02df6ad6c289d82/micronaut-ms/beer-websocket/src/main/java/beer/websocket/TransactionWebSocket.java#L34) 
when a new `beer-billing` application opens a websocket connection to `TransactionWebsocket` aka `/ws/` socket connection.
It it then asked to go off and shared all the sessions between all the running instances of beer-websocket. So beer-websocket instance 1 sends all its connected websockets sessions to 2 and 2 sends to 1 and so on.
The websocket connections that it sends is declared at the top of    `TransactionWebsocket` found as:  `public static ArrayList<WebSocketSession> sessions = new ArrayList<WebSocketSession>();`


[beer-billing: TransactionWebsocket.java](https://github.com/vahidhedayati/micronaut-playground/blob/dd13f9af63b764482d248c70e02df6ad6c289d82/micronaut-ms/beer-billing/src/main/java/micronaut/demo/beer/TransactionWebSocket.java)
This is triggered only by `beer-websocket` application and it is when a socket application is started after there is existing running billing applications.
This simply receives `hostname:port` of remote billing application as a message for which is directly connects back to as a `client websocket`.
When you stop a `beer-websocket` this application detects it and removes it from the local list of clients during the [Flowable ping sending which happens every 10 seconds](https://github.com/vahidhedayati/micronaut-playground/blob/dd13f9af63b764482d248c70e02df6ad6c289d82/micronaut-ms/beer-billing/src/main/java/micronaut/demo/beer/service/BootService.java#L57-L61).

 
You should find a test.sh script in the application which can be used to test above scenario 

```
./test.sh 
Serving beer to fred1 ------------------------------------------------------------
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
Billing fred1 ------------------------------------------------------------
{"cost":1872.0,"deskId":28375}{"cost":1872.0,"deskId":11156}{"cost":1872.0,"deskId":31617}{"cost":1872.0,"deskId":28375}
Serving beer to wilma1 ------------------------------------------------------------
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
Billing wilma1 ------------------------------------------------------------
{"cost":1872.0,"deskId":28375}{"cost":1872.0,"deskId":11156}{"cost":1872.0,"deskId":31617}{"cost":1872.0,"deskId":28375}
Serving beer to barney1 ------------------------------------------------------------
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
Billing barney1 ------------------------------------------------------------
{"cost":1872.0,"deskId":28375}{"cost":1872.0,"deskId":11156}{"cost":1872.0,"deskId":31617}{"cost":1872.0,"deskId":28375}
Serving beer to betty1 ------------------------------------------------------------
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
{"name":"mahou","size":"MEDIUM"}
Billing betty1 ------------------------------------------------------------
{"cost":1872.0,"deskId":28375}{"cost":1872.0,"deskId":11156}{"cost":1872.0,"deskId":31617}{"cost":1872.0,"deskId":28375}

```


The websockerts has transmitted the same cost to all 3 instances of the beer-billing application.


Branches are:

[Main branch: beerwebsocket-demo](https://github.com/vahidhedayati/micronaut-playground/tree/beerwebsocket-demo)
[basic: micronaut-ms-b4-kafka](https://github.com/vahidhedayati/micronaut-playground/tree/micronaut-ms-b4-kafka/micronaut-ms)

[kafka: ms-withkafka](https://github.com/vahidhedayati/micronaut-playground/tree/ms-withkafka/micronaut-ms)
 
[mongodb: mongodb](https://github.com/vahidhedayati/micronaut-playground/tree/mongodb/micronaut-ms)
 
 
[websocket demo standard websocket: video1-rev](https://github.com/vahidhedayati/micronaut-playground/tree/video1-rev)

  
And current master which is using `groovysocket` that processes beer-billing via websockets.

To get this latest code : `git clone https://github.com/vahidhedayati/micronaut-playground.git -b beerwebsocket-demo`

You can use  `git clone https://github.com/vahidhedayati/micronaut-playground.git -b micronaut-ms-b4-kafka`
or `git clone https://github.com/vahidhedayati/micronaut-playground.git -b ms-withkafka` or what ever branch name you desire to take
a look through - the files changes will be in this folder micronaut-ms folder of the main project above.





An issue was raised with Mauricio, can be found here: https://github.com/mfarache/micronaut-ms/issues/2


>All of this is really attempts to make the technology work, I am unsure if some of the things done are best practise.
>It is more theory based