#!/bin/bash

#############################################
# The very first install on a machine run this script
# This will simply put in place minikube kubectl setup the vm - add consul - zipkin mongo
# Then install each custom app in this project in to minikube vm 
#
# if you want to clean the slate without reinstalling it all refer to ./refresh-minikube.sh
###

#Please set this variable
DOCKER_USERNAME="vahidhedayati";

function clean_minikube() {
	echo "clearing out minikube"
	minikube delete
	minikube stop
	rm -rf $HOME/.minikube
}


echo "-----------------------------------------------------------------------------------"
echo "About to install virtualbox - docker and add kubernetes sources as well as install kubectl DEB package"
sudo apt-get  --yes --force-yes  install apt-transport-https virtualbox virtualbox-ext-pack docker docker.io 

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


wget https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

echo "-----------------------------------------------------------------------------------"
echo "Minikube added about to start it - this may take a while - please wait"


#minikube start
minikube start --cpus 4 --memory 4096

curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt update
sudo apt -y install kubectl

echo "Showing kubectl cluster info"
kubectl cluster-info


kubectl config view

kubectl get nodes

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



echo "-----------------------------------------------------------------------------------"
echo "Demo showing you minikub adons"
minikube addons list

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
git checkout v1.3.0
#cp $CURRENT_PATH/kubernetes/values.yaml ./
cd ../
helm install -f $CURRENT_PATH/kubernetes/helm-consul-values.yaml --name custom ./consul-helm
 
#echo "-----------------------------------------------------------------------------------"
#echo "Editing values.yaml and updating replicas/boostrapExpect values of 3  to 1 "
#ed -s values.yaml << EOF
#,s/replicas: 3/replicas: 1/g
#,s/bootstrapExpect: 3/bootstrapExpect: 1/g
#w
#q
#EOF


# No tiller - none of these fixes it
#minikube addons enable registry-creds
#kubectl logs --namespace kube-system tiller-deploy-2654728925-j2zvk

echo "-----------------------------------------------------------------------------------"
echo "Installing consul-helm using helm"

helm install .
# running the install twice appears to work - odd 
#cd ../
#helm install ./consul-helm

consul_dns=$(kubectl get svc |grep consul-dns|awk '{print $1}')
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  labels:
    addonmanager.kubernetes.io/mode: EnsureExists
  name: kube-dns
  namespace: kube-system
data:
  stubDomains: |
    {"consul": ["$(kubectl get svc $consul_dns -o jsonpath='{.spec.clusterIP}')"]}
EOF

function installKafka() {



echo "----------------------------"
echo "Starting docker zookeeper and kafka "
	
sudo docker run  -d -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:4.1.0
sudo docker run -d  -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:4.1.0

#sudo docker run -d --net=confluent --name=zookeeper --rm -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:5.0.1
#sudo docker run -d --net=confluent --name=kafka --rm -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:5.0.1
echo "----------------------"
echo "Applying kafka service files"
cd $CURRENT_PATH;
# https://dzone.com/articles/ultimate-guide-to-installing-kafka-docker-on-kuber  <<---- below as per this link
#kubectl create -f kubernetes/kafka/00-zookeeper.yml
#kubectl create -f kubernetes/kafka/05-kafka-service.yml
#kubectl create -f kubernetes/kafka/09-kafka-broker.yml
#kubectl  create -f kubernetes/kafka.yml
#kubectl create -f kubernetes/kafka/namespace.yml
#kubectl create -f kubernetes/kafka/zookeeper.yml
#kubectl create -f kubernetes/kafka/service.yml
kubectl create -f kubernetes/kafka/zookeeper_micro.yaml
kubectl create -f kubernetes/kafka/kafka_micro.yaml
kubectl create -f kubernetes/kafka/kafka_service.yaml




	#cd /tmp
	#rm -rf kubernetes-kafka
	#git clone https://github.com/Yolean/kubernetes-kafka.git
	#cd kubernetes-kafka
	#kubectl apply -f ./configure/minikube-storageclass-broker.yml
	#kubectl apply -f ./configure/minikube-storageclass-zookeeper.yml
	#kubectl apply -f ./00-namespace.yml 
	#kubectl apply -f ./rbac-namespace-default
	#kubectl apply -f ./zookeeper
	#kubectl apply -f ./kafka
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

function installMong() {
 sudo docker run -it -d mongo


#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/storageclass.yaml

#kubectl create -f https://github.com/vmware/kubernetes/blob/kube-examples/kube-examples/mongodb-shards/storage-volumes-node01.yaml
#kubectl create -f https://github.com/vmware/kubernetes/blob/kube-examples/kube-examples/mongodb-shards/storage-volumes-node02.yaml
#kubectl create -f https://github.com/vmware/kubernetes/blob/kube-examples/kube-examples/mongodb-shards/storage-volumes-node03.yaml
#kubectl create -f https://github.com/vmware/kubernetes/blob/kube-examples/kube-examples/mongodb-shards/storage-volumes-node04.yaml

#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node01-deployment.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node02-deployment.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node03-deployment.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node04-deployment.yaml

#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node01-service.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node02-service.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node03-service.yaml
#kubectl create -f https://raw.githubusercontent.com/vmware/kubernetes/kube-examples/kube-examples/mongodb-shards/node04-service.yaml
}
#installMong;


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

echo "-----------------------------------------------------------------------------------"
echo "Running gradle assemble in $CURRENT_PATH"
./gradlew assemble

echo "-----------------------------------------------------------------------------------"
echo "Attempting to install each individual app dynamically - refer to below to automate process of updating each instance when there is code changes"

chmod 755 ./install-app.sh

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-billing beer-billing billing $DOCKER_USERNAME"
./install-app.sh beer-billing beer-billing billing $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-waiter beer-waiter waiter $DOCKER_USERNAME"
./install-app.sh beer-waiter beer-waiter waiter $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: ./install-app.sh beer-stock beer-stock stock  $DOCKER_USERNAME"
./install-app.sh beer-stock beer-stock stock  $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh beer-front beer-front front  $DOCKER_USERNAME"
./install-app.sh beer-front beer-front front  $DOCKER_USERNAME

echo "-----------------------------------------------------------------------------------"
echo "running: sh ./install-app.sh frontend/react beer-react react $DOCKER_USERNAME 1 3000"
./install-app.sh frontend/react beer-react react $DOCKER_USERNAME 1 3000


echo "-----------------------------------------------------------------------------------"
#echo "running kubectl apply -f front-ingres.yml"
#kubectl apply -f front-ingres.yml 


sleep 120;

echo "-----------------------------------------------------------------------------------"
REACT_HOST=$(kubectl get pods |grep "react"|awk '{print $1}');
echo "Porting forwarding $REACT_HOST 3000:3000"
kubectl port-forward $REACT_HOST 3000:3000&


echo "-----------------------------------------------------------------------------------"
FRONT_HOST=$(kubectl get pods |grep "front"|awk '{print $1}');
echo "Porting forwarding $FRONT_HOST 8080:8080"
kubectl port-forward $FRONT_HOST 8080:8080&


#echo "-----------------------------------------------------------------------------------"
#echo "Launching minikube dashboard"
#minikube dashboard&



