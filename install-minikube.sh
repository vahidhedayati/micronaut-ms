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
minikube start --cpus 4 --memory 4096 --kubernetes-version v1.10.0 --bootstrapper=kubeadm

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


function installConsulHelm() {
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

cd /tmp
echo "-----------------------------------------------------------------------------------"
echo "Installing consul-helm"
rm -rf /tmp/consul-helm
git clone https://github.com/hashicorp/consul-helm.git
cd consul-helm
git checkout v0.4.0

helm install --name consul ./

#cp $CURRENT_PATH/kubernetes/values.yaml ./

#echo "-----------------------------------------------------------------------------------"
#echo "Editing values.yaml and updating replicas/boostrapExpect values of 3  to 1 "
#ed -s values.yaml << EOF
#,s/replicas: 3/replicas: 1/g
#,s/bootstrapExpect: 3/bootstrapExpect: 1/g
##w
##q
#EOF

helm del --purge consul
#helm install --name consul ./

cd ../
helm install -f $CURRENT_PATH/kubernetes/helm-consul-values.yaml --name custom ./consul-helm
# helm install ./consul-helm



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
installConsulHelm;
#exit;




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

installKafka;



function loadMongo() {
    sudo docker run -it -d mongo
	cd $CURRENT_PATH;
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



