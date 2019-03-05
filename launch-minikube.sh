#!/bin/bash

DOCKER_USERNAME="vahidhedayati"

#Once it has been installed next run

minikube stop

sudo docker run -d -p 9411:9411 openzipkin/zipkin

minikube start  --memory 4096
# --cpus 4 --memory 4096 --kubernetes-version v1.10.0 --bootstrapper=kubeadm

kubectl config use-context minikube

echo "-----------------------------------------------------------------------------------"
echo "logging into minikube VM and adding restart-image.sh script"
echo "run minikube ssh then when logged in : restart-image.sh front restart-image.sh stock"
minikube ssh << EOF
sudo bash
cat <<EEF>> /usr/bin/restart-image.sh
#!/bin/bash
sudo docker ps |grep $(echo '\$1')|grep POD|awk '{print "sudo docker kill "$(echo '\$NF')}'|/bin/sh
EEF
chmod 755 /usr/bin/restart-image.sh
exit
exit
EOF

function loadKafka() {
	#sudo docker run -d --net=confluent --name=zookeeper --rm -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:5.0.1


	#sudo docker run -d --net=confluent --name=kafka --rm -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:5.0.1
	
sudo docker run  -d -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:4.1.0
sudo docker run -d  -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:4.1.0




}

#loadKafka;


function loadMongo() {
	sudo docker run -it -d mongo
	kubectl expose deployment mongodb --type=LoadBalancer
}
#loadMongo;



sleep 60

CONSUL_HOST=$(kubectl get pods |grep server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Porting forwarding $CONSUL_HOST 8500:8500"
kubectl port-forward $CONSUL_HOST 8500:8500&


