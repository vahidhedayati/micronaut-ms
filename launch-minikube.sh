#!/bin/bash

#Once it has been installed next run

minikube start

sleep 120

CONSUL_HOST=$(kubectl get pods |grep server|awk '{print $1}');

echo "-----------------------------------------------------------------------------------"
echo "Porting forwarding $CONSUL_HOST 8500:8500"
kubectl port-forward $CONSUL_HOST 8500:8500&


echo "-----------------------------------------------------------------------------------"
REACT_HOST=$(kubectl get pods |grep "react-deployment"|awk '{print $1}');
echo "Porting forwarding $REACT_HOST 3000:3000"
kubectl port-forward $REACT_HOST 3000:3000&


#echo "-----------------------------------------------------------------------------------"
#FRONT_HOST=$(kubectl get pods |grep "front-deployment"|awk '{print $1}');
#echo "Porting forwarding $FRONT_HOST 8080:8080"
#kubectl port-forward $FRONT_HOST 8080:8080&

