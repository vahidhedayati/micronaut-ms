#!/bin/bash

######
# Called by ./refresh-minikube.sh

FOLDER=$1
APP=$2
NAME=$3
DOCKERHOME=$4
REPLICAS=$5
APP_PORT=$6

currentVersion=$(sudo docker images|grep $NAME|awk '{if ($2 !~ /<none>/ && $2 !~ /latest/) { n=$1; v=$2; print v} }'|sort -t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr|head -n1)

echo "Current version : $currentVersion"

VERSION=$(echo $currentVersion|awk '{print (!$0 )? "0.0" :$0 }')


if [[ $APP_PORT == "" ]]; then
        APP_PORT=$(grep port $APP/src/main/resources/application.*|awk -F":" '{if ($2 !~ /^#/) { v=$2; ccount=index(v,"=")+1; print substr(v,ccount,length(v)) } }')
        echo "APP PORT NOT DEFINED DYNAMICALL SET TO $APP_PORT"
fi

if [[ $REPLICAS == "" ]]; then
        REPLICAS=1
        echo "REPLICAS NOT DEFINED DEFAULTING TO 1"
fi


CONSUL_HOST=$(kubectl get svc |grep consul-server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Overwriting $NAME.yaml"
>$NAME.yaml

echo "Reproducing $NAME.yaml"
cat <<EOF>>$NAME.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $NAME-deployment
  labels:
    app: $NAME
spec:
  replicas: $REPLICAS
  selector:
    matchLabels:
      app: $NAME
  template:
    metadata:
      labels:
        app: $NAME
    spec:
      containers:
      - name: waiter
        image: $DOCKERHOME/$APP:$VERSION
        env:
        - name: CONSUL_HOST
          value: "$CONSUL_HOST"
        - name: KAFKA_HOST
          value: "kafka-service"
        - name: ZIPKIN_HOST
          value: "zipkin-deployment"
        - name: MONGO_HOST
          value: "mongodb"
        ports:
        - containerPort: $APP_PORT
EOF



echo "-----------------------------------------------------------------------------------"
echo "Running: kubectl apply -f  $NAME.yaml"
kubectl apply -f $NAME.yaml

echo "-----------------------------------------------------------------------------------"
echo "Exposing $NAME-deployment on port $APP_PORT"
# kubectl expose pods/$(kubectl get pods |grep "$NAME-deployment"|awk '{print $1}') --type="NodePort" --port $APP_PORT

kubectl delete service $NAME-deployment


kubectl expose deployment $NAME-deployment --port=$APP_PORT --target-port=$APP_PORT



