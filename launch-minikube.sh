#!/bin/bash

DOCKER_USERNAME="vahidhedayati"

#Once it has been installed next run

minikube stop

sudo docker run -d -p 9411:9411 openzipkin/zipkin

minikube start --cpus 4 --memory 4096


function loadKafka() {
	#sudo docker run -d --net=confluent --name=zookeeper --rm -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:5.0.1


	#sudo docker run -d --net=confluent --name=kafka --rm -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:5.0.1
	
sudo docker run  -d -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:4.1.0
sudo docker run -d  -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:4.1.0




}

loadKafka;


function loadMongo() {
	sudo docker run -it -d mongo
	kubectl expose deployment mongodb --type=LoadBalancer
}
loadMongo;



sleep 60

CONSUL_HOST=$(kubectl get pods |grep server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Porting forwarding $CONSUL_HOST 8500:8500"
kubectl port-forward $CONSUL_HOST 8500:8500&

sleep 60

echo "-----------------------------------------------------------------------------------"
REACT_HOST=$(kubectl get pods |grep "react-deployment"|awk '{print $1}');
echo "Porting forwarding $REACT_HOST 3000:3000"
kubectl port-forward $REACT_HOST 3000:3000&


echo "-----------------------------------------------------------------------------------"
FRONT_HOST=$(kubectl get pods |grep "front-deployment"|awk '{print $1}');
echo "Porting forwarding $FRONT_HOST 8080:8080"
kubectl port-forward $FRONT_HOST 8080:8080&



