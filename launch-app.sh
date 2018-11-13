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



echo "-----------------------------------------------------------------------------------"
echo "Running: kubectl apply -f  $NAME.yaml"
kubectl apply -f $NAME.yaml


#echo "Running: kubectl port-forward $(kubectl get pods |grep $NAME-deployment|awk '{print $1}') $APP_PORT:$APP_PORT&"
#kubectl port-forward $(kubectl get pods |grep "$NAME-deployment"|awk '{print $1}') $APP_PORT:$APP_PORT&


