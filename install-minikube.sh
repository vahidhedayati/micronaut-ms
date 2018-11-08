#!/bin/bash

#Please set this variable
DOCKER_USERNAME="vahidhedayati";



if [[ ! -f $HOME/.docker/config.json  ]]; then 
	echo "You must goto https://hub.docker.com/"
	echo "Register then run "
	echo "docker login on your PC first before proceeding"
	exit;
fi


if [[ $DOCKER_USERNAME == "" ]]; then
	echo "You must edit this script and assign your docker username as the variable DOCKER_USERNAME=\"yourusername\""
	exit
fi


echo "About to install virtualbox - docker and add kubernetes sources as well as install kubectl DEB package"

sudo apt-get  --yes --force-yes  install apt-transport-https virtualbox virtualbox-ext-pack docker docker.io 

wget https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt update
sudo apt -y install kubectl

echo "Showing kubectl cluster info"
kubectl cluster-info


kubectl config view

kubectl get nodes


minikube ssh << EOF
exit
EOF


echo "Demo showing you minikub adons"
minikube addons list

echo "Enabling minikube ingres - inbuilt HTTP server"
minikube addons enable ingress


minikube start
 
kubectl config use-context minikube


cd /tmp
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get > install-helm.sh

chmod u+x install-helm.sh

echo "About to install HELM"
./install-helm.sh

echo "Initialising helm"
helm init

echo "----- Installing consul-helm"
git clone https://github.com/hashicorp/consul-helm.git
cd consul-helm
git checkout v0.1.0

echo "Editing values.yaml and updating replicas/boostranExpect values of 3  to 1 
ed -s values.yaml << EOF
,s/replicas: 3/replicas: 1/g
,s/bootstrapExpect: 3/bootstrapExpect: 1/g
w
q
EOF



echo "Installing consul-helm using helm"
cd ../
helm install ./consul-helm



kubectl get pods

kubectl get svc

CONSUL_HOST=$(kubectl get pods |grep server|grep Running|awk '{print $1}');

echo "Porting forwarding $CONSUL_HOST 8500:8500"
kubectl port-forward $CONSUL_HOST 8500:8500

echo "running docker / zipkin"
docker run -d -p 9411:9411 openzipkin/zipkin

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

echo "Applying ziping yaml file "
kubectl apply -f zipkin.yaml

echo "Exposing zipkin port 9411"
kubectl expose deployment/zipkin-deployment --type="NodePort" --port 9411


echo "Running gradle assemble in micronaut-ms folder"
./gradlew assemble

echo "Attempting to install each individual app dynamically - refer to below to automate process of updating each instance when there is code changes"
sh ./install-app.sh beer-billing beer-billing billing $DOCKER_USERNAME
sh ./install-app.sh beer-waiter beer-waiter waiter $DOCKER_USERNAME
sh ./install-app.sh beer-stock beer-stock stock  $DOCKER_USERNAME
sh ./install-app.sh beer-front beer-front front  $DOCKER_USERNAME
sh ./install-app.sh frontend/react beer-react react $DOCKER_USERNAME 1 3000


echo "Launching minikube dashboard"
minikube dashboard&


