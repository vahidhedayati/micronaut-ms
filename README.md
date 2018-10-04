Forked from: https://github.com/mfarache/micronaut-ms. The project did not appear to work for me so I have made it from scratch taking the content of the other project:


Please note under development - Below instructions will currently not work
--- 

Been built into a micronaut project on micronaut:
```
 mn --version
| Micronaut Version: 1.0.0.M4
| JVM Version: 1.8.0_171
```

Please ensure you have MongoDB installed locally
----

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

3. Then launch separate instances again
```
./gradlew beer-waiter:run  beer-billing:run beer-stock --parallel
```


4. Then launch separate instances again (This now making 3 instances of billing / waiter / stock all running )
```
./gradlew beer-waiter:run  beer-billing:run beer-stock --parallel
```



Videos
----


1. [Basic run through and fallover explained](https://www.youtube.com/watch?v=J_U7tuWy-C0)



To launch the site call http://localhost:3000 on the browser

