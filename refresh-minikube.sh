#!/bin/bash

####
# Cut down version of install which removes minikube local container - adds all the stuff back in i.e.
# consul / mongo / zipkin 
# since consul is updated - ./refresh-app.sh is called which updates each apps yaml file without re-uploading the old app file 
####

#Please set this variable
DOCKER_USERNAME="vahidhedayati";

function clean_minikube() {
	echo "clearing out minikube"
	minikube delete
	minikube stop
	rm -rf $HOME/.minikube
}


if [[ ! -f $HOME/.docker/config.json  ]]; then 
	echo "You must goto https://hub.docker.com/"
	echo "Register then run "
	echo "docker login on your PC first before proceeding"
	exit;
fi

CURRENT_PATH=$(pwd);
if [[ $DOCKER_USERNAME == "" ]]; then
	echo "You must edit this script and assign your docker username as the variable DOCKER_USERNAME=\"yourusername\""
	exit
fi

clean_minikube

echo "-----------------------------------------------------------------------------------"
echo "About to install virtualbox - docker and add kubernetes sources as well as install kubectl DEB package"


minikube start --cpus 4 --memory 4096


echo "-----------------------------------------------------------------------------------"
echo "Enabling minikube ingres - inbuilt HTTP server"
minikube addons enable ingress

kubectl config use-context minikube

cd /tmp
#curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get > install-helm.sh
#chmod u+x install-helm.sh
#./install-helm.sh
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get > get_helm.sh
chmod 700 get_helm.sh


echo "About to install HELM"
./get_helm.sh

echo "Initialising helm"
helm init --wait



cd /tmp
echo "-----------------------------------------------------------------------------------"
echo "Installing consul-helm"
rm -rf /tmp/consul-helm
git clone https://github.com/hashicorp/consul-helm.git
cd consul-helm
git checkout v0.1.0
 
echo "-----------------------------------------------------------------------------------"
echo "Editing values.yaml and updating replicas/boostranExpect values of 3  to 1 "
ed -s values.yaml << EOF
,s/replicas: 3/replicas: 1/g
,s/bootstrapExpect: 3/bootstrapExpect: 1/g
w
q
EOF


echo "-----------------------------------------------------------------------------------"
echo "Installing consul-helm using helm"

helm install .


function installKafka() {



echo "----------------------------"
echo "Starting docker zookeeper and kafka "
	
sudo docker run  -d -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:4.1.0
sudo docker run -d  -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:4.1.0

echo "----------------------"
echo "Applying kafka service files"
cd $CURRENT_PATH;
# https://dzone.com/articles/ultimate-guide-to-installing-kafka-docker-on-kuber  <<---- below as per this link
kubectl create -f kubernetes/kafka/00-zookeeper.yml
kubectl create -f kubernetes/kafka/05-kafka-service.yml
kubectl create -f kubernetes/kafka/09-kafka-broker.yml
}


installKafka;


function loadMongo() {
        sudo docker run -it -d mongo
	#curl -L https://github.com/kubernetes/kompose/releases/download/v1.11.0/kompose-darwin-amd64 -o kompose
	#chmod +x kompose
	#sudo mv ./kompose /usr/local/bin/kompose
	cd $CURRENT_PATH;
	
	#cd kubernetes/mongo
	#kompose convert -f docker-compose.yaml
	##Above will generate 3 yaml files
	kubectl create -f kubernetes/mongo/00-mongo-persistant.yml
	kubectl create -f kubernetes/mongo/05-mongo-deployment.yml
	kubectl create -f kubernetes/mongo/10-mongo-service.yml
	kubectl expose deployment mongodb --type=LoadBalancer

}
loadMongo;


kubectl get pods

kubectl get svc

echo "sleeping for a bit"
sleep 20;
CONSUL_HOST=$(kubectl get pods |grep consul-server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Porting forwarding $CONSUL_HOST 8500:8500"
kubectl port-forward $CONSUL_HOST 8500:8500&

echo "-----------------------------------------------------------------------------------"
echo "running docker / zipkin"
sudo docker run -d -p 9411:9411 openzipkin/zipkin

echo "-----------------------------------------------------------------------------------"
echo  "Generating zipkin.yaml file"
>zipkin.yaml

cat <<EOF>>zipkin.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zipkin-deployment
  labels:
    app: zipkin
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zipkin
  template:
    metadata:
      labels:
        app: zipkin
    spec:
      containers:
      - name: zipkin
        image: openzipkin/zipkin:latest
        ports:
        - containerPort: 9411
EOF

echo "-----------------------------------------------------------------------------------"
echo "Applying ziping yaml file "
kubectl apply -f zipkin.yaml

echo "-----------------------------------------------------------------------------------"
echo "Exposing zipkin port 9411"
kubectl expose deployment/zipkin-deployment --type="NodePort" --port 9411

cd $CURRENT_PATH;
chmod 755 ./refresh-app.sh

echo "-----------------------------------------------------------------------------------"
echo "Attempting to install each individual app dynamically - refer to below to automate process of updating each instance when there is code changes"


echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-billing beer-billing billing $DOCKER_USERNAME"
./refresh-app.sh beer-billing beer-billing billing $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-waiter beer-waiter waiter $DOCKER_USERNAME"
./refresh-app.sh beer-waiter beer-waiter waiter $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: ./install-app.sh beer-stock beer-stock stock  $DOCKER_USERNAME"
./refresh-app.sh beer-stock beer-stock stock  $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-front beer-front front  $DOCKER_USERNAME"
./refresh-app.sh beer-front beer-front front  $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh frontend/react beer-react react $DOCKER_USERNAME 1 3000"
./refresh-app.sh frontend/react beer-react react $DOCKER_USERNAME 1 3000


echo "-----------------------------------------------------------------------------------"
echo "running kubectl apply -f front-ingres.yml"
kubectl apply -f front-ingres.yml 


sleep 120;

echo "-----------------------------------------------------------------------------------"
REACT_HOST=$(kubectl get pods |grep "react-deployment"|awk '{print $1}');
echo "Porting forwarding $REACT_HOST 3000:3000"
kubectl port-forward $REACT_HOST 3000:3000&


echo "-----------------------------------------------------------------------------------"
FRONT_HOST=$(kubectl get pods |grep "front-deployment"|awk '{print $1}');
echo "Porting forwarding $FRONT_HOST 8080:8080"
kubectl port-forward $FRONT_HOST 8080:8080&


#echo "-----------------------------------------------------------------------------------"
#echo "Launching minikube dashboard"
#minikube dashboard&



