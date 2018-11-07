#!/bin/bash

FOLDER=$1
APP=$2
NAME=$3
DOCKERHOME=$4
REPLICAS=$5
APP_PORT=$6


currentVersion=$(sudo docker images|grep $NAME|awk '{if ($2 !~ /<none>/ && $2 !~ /latest/) { n=$1; v=$2; print v} }'|sort -nrk2|head -n1)
current=$(echo $currentVersion|awk '{print (!$0 )? "0.0" :$0 }')


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

echo "APP $APP current = $current "
# increment_version
VERSION=$(increment_version);
echo "NEW VERSION = $VERSION"


if [[ $APP_PORT == "" ]]; then 
	APP_PORT=$(grep port $APP/src/main/resources/application.*|awk -F":" '{if ($2 !~ /^#/) { v=$2; ccount=index(v,"=")+1; print substr(v,ccount,length(v)) } }')
	echo "APP PORT NOT DEFINED DYNAMICALL SET TO $APP_PORT"
fi

if [[ $REPLICAS == "" ]]; then 
	REPLICAS=1
	echo "REPLICAS NOT DEFINED DEFAULTING TO 1"
fi

echo "running: cd $FOLDER"
cd $FOLDER
echo "running: docker build -t $APP ."
docker build -t $APP .

echo "running: docker tag $APP $DOCKERHOME/$APP:$VERSION"
sudo docker tag $APP $DOCKERHOME/$APP:$VERSION

echo "running sudo docker push $DOCKERHOME/$APP:$VERSION"
sudo docker push $DOCKERHOME/$APP:$VERSION

echo "running: cd.."
cd ..

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
          value: "consul-server"
        - name: ZIPKIN_HOST
          value: "zipkin-deployment"
        ports:
        - containerPort: $APP_PORT
EOF

echo "Exposing $NAME-deployment on port $APP_PORT"
kubectl expose pods/$(kubectl get pods |grep "$NAME-deployment"|grep Running|awk '{print $1}') --type="NodePort" --port $APP_PORT



