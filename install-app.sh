#!/bin/bash

FOLDER=$1
APP=$2
NAME=$3
DOCKERHOME=$4
APP_PORT=$5
REPLICAS=$6


currentVersion=$(sudo docker images|grep $NAME|awk '{if ($2 !~ /<none>/) { n=$1; v=$2; print v} }'|sort -nrk2|head -n1)
current=echo $currentVersion|awk '{print (!$0 )? "0.0" :$0 }'

echo "APP $APP current = $current "
VERISON=increment_version()

if [[ $APP_PORT == "" ]]; then 
	APP_PORT=$(grep port $APP/src/main/resources/application.*|awk -F":" '{if ($2 !~ /^#/) { v=$2; ccount=index(v,"=")+1; print substr(v,ccount,length(v)) } }')
	echo "APP PORT NOT DEFINED DYNAMICALL SET TO $APP_PORT"
fi

if [[ $REPLICAS == "" ]]; then 
	REPLICAS=1
	echo "REPLICAS NOT DEFINED DEFAULTING TO 1"
fi

cd $FOLDER
sudo docker build -t $APP .
cd ..

>$NAME.yaml
cat <<EOF>>$NAME.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $APP
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


function increment_version() {
 local v=$VERSION
 if [ -z $2 ]; then 
    local rgx='^((?:[0-9]+\.)*)([0-9]+)($)'
 else 
    local rgx='^((?:[0-9]+\.){'$(($2-1))'})([0-9]+)(\.|$)'
    for (( p=`grep -o "\."<<<".$v"|wc -l`; p<$2; p++)); do 
       v+=.0; done; fi
 val=`echo -e "$v" | perl -pe 's/^.*'$rgx'.*$/$2/'`
 echo "$v" | perl -pe s/$rgx.*$'/${1}'`printf %0${#val}s $(($val+1))`/
}

