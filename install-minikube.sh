#!/bin/bash

#############################################
# The very first install on a machine run this script
# This will simply put in place minikube kubectl setup the vm - add consul - zipkin mongo
# Then install each custom app in this project in to minikube vm 
#
# if you want to clean the slate without reinstalling it all refer to ./refresh-minikube.sh
###


function clean_minikube() {
	echo "clearing out minikube"
	minikube delete
	minikube stop
	rm -rf $HOME/.minikube
}

echo "-----------------------------------------------------------------------------------"
echo "About to install virtualbox - docker and add kubernetes sources as well as install kubectl DEB package"
sudo apt-get  --yes --force-yes  install apt-transport-https virtualbox virtualbox-ext-pack docker docker.io


if sudo grep -q 'auths": {}' ~/.docker/config.json ; then
        echo "You must goto https://hub.docker.com/"
        echo "Register then run "
        echo "docker login on your PC first before proceeding"
        exit;
fi

DOCKER_USERNAME=$(sudo docker info | sed '/Username:/!d;s/.* //');


CURRENT_PATH=$(pwd);
if [[ $DOCKER_USERNAME == "" ]]; then
        echo "You must edit this script and assign your docker username as the variable DOCKER_USERNAME=\"yourusername\""
        exit
fi

clean_minikube

function installBase() {

wget https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

echo "-----------------------------------------------------------------------------------"
echo "Minikube added about to start it - this may take a while - please wait"

curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt update
sudo apt -y install kubectl

}
#installBase;

#minikube start
#minikube start --cpus 4 --memory 4096 --kubernetes-version v1.13.0 --bootstrapper=kubeadm
minikube start  --memory 4096 
#--kubernetes-version v1.13.0 --bootstrapper=kubeadm

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


function installHelm() {
cd /tmp
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get > install-helm.sh
chmod u+x install-helm.sh
./install-helm.sh
#curl https://raw.githubusercontent.com/helm/helm/master/scripts/get > get_helm.sh
#chmod 700 get_helm.sh


#echo "About to install HELM"
#./get_helm.sh

echo "Initialising helm"
helm init --wait

}
installHelm

function installConsulHelm() {
cd /tmp
echo "-----------------------------------------------------------------------------------"
echo "Installing consul-helm"
rm -rf /tmp/consul-helm
git clone https://github.com/hashicorp/consul-helm.git
cd consul-helm
git checkout v0.5.0

helm install --name consul ./


helm del --purge consul

cd ../
helm install -f $CURRENT_PATH/kubernetes/helm-consul-values.yaml --name consul ./consul-helm
}
installConsulHelm;


function installConsulConfigMap() {
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
}
installConsulConfigMap

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

    kubectl create -f kubernetes/kafka/zookeeper_micro.yaml
    kubectl create -f kubernetes/kafka/kafka_micro.yaml
    kubectl create -f kubernetes/kafka/kafka_service.yaml

}

#installKafka;



function loadMongo() {
    sudo docker run -it -d mongo
	cd $CURRENT_PATH;
	kubectl create -f kubernetes/mongo/00-mongo-persistant.yml
	kubectl create -f kubernetes/mongo/05-mongo-deployment.yml
	kubectl create -f kubernetes/mongo/10-mongo-service.yml
	kubectl expose deployment mongodb --type=LoadBalancer
}
#loadMongo;


kubectl get pods

kubectl get svc

echo "sleeping for a bit"
sleep 20;
CONSUL_HOST=$(kubectl get pods |grep consul-server|head -n1|awk '{print $1}');

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


sleep 120;

#echo "-----------------------------------------------------------------------------------"
#echo "Launching minikube dashboard"
#minikube dashboard&



