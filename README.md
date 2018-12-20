A basic version of the MS project back to two applications a test for allowing two basic apps to interact via consul using Kubernetes 

```
# for the very first time
./install-minikube.sh 


# for any time beyond this to relaunch it A fresh reboot of physical machine 
./launch-minikube.sh 
```


Once launched

---

Above demonstrates the same issue as bigger version of same app where consul is not allowing interaction

To do the same thing manually first run consul 

To run first either install consul locally and run ./consul agent dev or if you have installed docker simply run 

```
sudo docker run -p 8500:8500 consul
```

To run 1 instance of thes beer billing waiter instances run this from within this project folder:

```
cd micronaut-ms



./gradlew beer-waiter:run  beer-billing:run --parallel

```

Now hit browser on 
```
http://localhost:8084/waiter/beer/fred
```



