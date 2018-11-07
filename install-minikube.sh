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


./install-app.sh beer-billing beer-billing billing vahidhedayati
./install-app.sh beer-waiter beer-waiter waiter vahidhedayati
./install-app.sh beer-stock beer-stock stock vahidhedayati
./install-app.sh beer-front beer-front front vahidhedayati
./install-app.sh frontend/react beer-react react vahidhedayati 1 3000


minikube dashboard&

