#!/bin/bash

sudo apt-get  --yes --force-yes  install apt-transport-https virtualbox virtualbox-ext-pack docker docker.io 

wget https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt update
sudo apt -y install kubectl

kubectl cluster-info

kubectl config view

kubectl get nodes


minikube ssh << EOF
exit
EOF


minikube addons list

cd /tmp
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get > install-helm.sh

chmod u+x install-helm.sh

./install-helm.sh

helm init

git clone https://github.com/hashicorp/consul-helm.git
cd consul-helm
git checkout v0.1.0
cd ../
helm install ./consul-helm



kubectl get pods

kubectl get svc


docker run -d -p 9411:9411 openzipkin/zipkin

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


kubectl apply -f zipkin.yaml

kubectl expose deployment/zipkin-deployment --type="NodePort" --port 9411


./gradlew assemble

cd beer-billing
sudo docker build -t beer-billing .

cd ../beer-waiter/
sudo docker build -t beer-waiter .

cd ../beer-stock/
sudo docker build -t beer-stock .

cd ../beer-front/
sudo docker build -t beer-front .

lastImage=$(sudo docker images|grep beer-react|awk '{if ($2 !~ /<none>/) { n=$1; v=$2; print n"\t"v} }'|sort -nrk2|head -n1);
cd ../frontend/react/
sudo docker build -t react .



>waiter.yaml
cat <<EOF>>waiter.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: waiter-deployment
  labels:
    app: waiter
spec:
  replicas: 1
  selector:
    matchLabels:
      app: waiter
  template:
    metadata:
      labels:
        app: waiter
    spec:
      containers:
      - name: waiter
        image: vahidhedayati/beer-waiter:1.2
        env:
        - name: CONSUL_HOST
          value: "consul-server"
        - name: ZIPKIN_HOST
          value: "zipkin-deployment"
        ports:
        - containerPort: 8084
EOF




minikube dashboard&


