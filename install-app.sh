#!/bin/bash
############
# Called by ./install-minikube.sh - for each app running in this project 
##############
FOLDER=$1
APP=$2
NAME=$3
DOCKERHOME=$4
REPLICAS=$5
APP_PORT=$6

CURRENT_PATH=$(pwd)
currentVersion=$(sudo docker images|grep $NAME|awk '{if ($2 !~ /<none>/ && $2 !~ /latest/) { n=$1; v=$2; print v} }'|sort -t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr|head -n1)

echo "Current version : $currentVersion"

current=$(echo $currentVersion|awk '{print (!$0 )? "0.0" :$0 }')
echo "Current revision $current"

function increment_version() {
 local v=$current
 if [ -z $2 ]; then 
    local rgx='^((?:[0-9]+\.)*)([0-9]+)($)'
 else 
    local rgx='^((?:[0-9]+\.){'$(($2-1))'})([0-9]+)(\.|$)'
    for (( p=`grep -o "\."<<<".$v"|wc -l`; p<$2; p++)); do 
       v+=.0; done; fi
 val=`echo -e "$v" | perl -pe 's/^.*'$rgx'.*$/$2/'`
 echo "$v" | perl -pe s/$rgx.*$'/${1}'`printf %0${#val}s $(($val+1))`/
}

echo "-----------------------------------------------------------------------------------"
echo "APP $APP current = $current "
# increment_version
VERSION=$(increment_version);

echo "-----------------------------------------------------------------------------------"
echo "NEW VERSION = $VERSION"


if [[ $APP_PORT == "" ]]; then 
	APP_PORT=$(grep port $APP/src/main/resources/application.*|awk -F":" '{if ($2 !~ /^#/) { v=$2; ccount=index(v,"=")+1; print substr(v,ccount,length(v)) } }'|head -n1)
	echo "APP PORT NOT DEFINED DYNAMICALL SET TO $APP_PORT"
fi

if [[ $REPLICAS == "" ]]; then 
	REPLICAS=1
	echo "REPLICAS NOT DEFINED DEFAULTING TO 1"
fi

echo "-----------------------------------------------------------------------------------"
echo "running: cd $FOLDER"
cd $FOLDER
echo "running: docker build -t $APP ."
sudo docker build -t $APP .

echo "-----------------------------------------------------------------------------------"
echo "running: docker tag $APP $DOCKERHOME/$APP:$VERSION"
sudo docker tag $APP $DOCKERHOME/$APP:$VERSION

echo "-----------------------------------------------------------------------------------"
echo "running sudo docker push $DOCKERHOME/$APP:$VERSION"
sudo docker push $DOCKERHOME/$APP:$VERSION

echo "running: cd $CURRENT_PATH"
cd $CURRENT_PATH


CONSUL_HOST=$(kubectl get svc |grep consul-server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Overwriting $NAME.yaml"
>$NAME.yaml
#  annotations:
#    "consul.hashicorp.com/connect-inject": "false"
#    "consul.hashicorp.com/connect-service-port": "$APP_PORT"
echo "Reproducing $NAME.yaml"
cat <<EOF>>$NAME.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $NAME
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
echo "Running: kubectl apply -f $NAME.yaml"
kubectl apply -f $NAME.yaml

echo "-----------------------------------------------------------------------------------"
echo "Exposing $NAME on port $APP_PORT"
# kubectl expose pods/$(kubectl get pods |grep "$NAME"|awk '{print $1}') --type="NodePort" --port $APP_PORT

kubectl delete service $NAME


#kubectl expose deployment $NAME --port=$APP_PORT --target-port=$APP_PORT
kubectl expose deployment/$NAME  --type="NodePort" --port $APP_PORT


#echo "Running: kubectl port-forward $(kubectl get pods |grep $NAME|awk '{print $1}') $APP_PORT:$APP_PORT&"

#kubectl port-forward $(kubectl get pods |grep "$NAME"|awk '{print $1}') $APP_PORT:$APP_PORT&


#echo "-----------------------------------------------------------------------------------"
echo "running kubectl apply -f $NAME-ingres.yml"
kubectl apply -f $NAME-ingres.yml


