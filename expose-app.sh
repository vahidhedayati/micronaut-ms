#!/bin/bash

APP=$1
NAME=$2
APP_PORT=$3

if [[ $APP_PORT == "" ]]; then
        APP_PORT=$(grep port $APP/src/main/resources/application.*|awk -F":" '{if ($2 !~ /^#/) { v=$2; ccount=index(v,"=")+1; print substr(v,ccount,length(v)) } }')
        echo "APP PORT NOT DEFINED DYNAMICALL SET TO $APP_PORT"
fi

echo "---------------------------------------------------------------------------
--------"
podName=$(kubectl get pods |grep "$NAME-deployment"|grep Running|awk '{print $1}')
echo "Exposing $NAME-deployment on port $APP_PORT -- $podName"
echo "running: kubectl expose pods/$podName --type="NodePort" --port $APP_PORT"
kubectl expose pods/$podName --type="NodePort" --port $APP_PORT

