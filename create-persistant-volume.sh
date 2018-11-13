#!/bin/bash


####################
# This is a script to automate creation of a persistant volume on your kubernetes VM - via minikube
# from https://kubernetes.io/docs/tasks/configure-pod-container/configure-persistent-volume-storage/
# ./create-persistant-volume.sh /mnt/data


MOUNT_POINT=$1


minikube ssh << EOF
sudo bash
mkdir -p $MOUNT_POINT
echo 'Hello from Kubernetes storage' > $MOUNT_POINT/index.html
exit
exit
EOF

CURRENT_PATH=$(pwd)
mkdir -p kubernetes/persistant-volume
cd  kubernetes/persistant-volume
> pv-volume.yaml
echo "-----------------------------------------------------------------------------------"
echo  "Generating pv-volume.yaml file"
>pv-volume.yaml

cat <<EOF>>pv-volume.yaml
kind: PersistentVolume
apiVersion: v1
metadata:
  name: task-pv-volume
  labels:
    type: local
spec:
  storageClassName: manual
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "$MOUNT_POINT"
EOF


# kubectl create -f https://k8s.io/examples/pods/storage/pv-volume.yaml
kubectl create -f ./pv-volume.yaml

kubectl get pv task-pv-volume

>pv-claim.yaml
cat <<EOF>>pv-claim.yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: task-pv-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 3Gi
EOF

#kubectl create -f https://k8s.io/examples/pods/storage/pv-claim.yaml
kubectl create -f ./pv-claim.yaml

kubectl get pv task-pv-volume


>pv-pod.yaml
cat <<EOF>>pv-pod.yaml
kind: Pod
apiVersion: v1
metadata:
  name: task-pv-pod
spec:
  volumes:
    - name: task-pv-storage
      persistentVolumeClaim:
       claimName: task-pv-claim
  containers:
    - name: task-pv-container
      image: nginx
      ports:
        - containerPort: 80
          name: "http-server"
      volumeMounts:
        - mountPath: "/usr/share/nginx/html"
          name: task-pv-storage
EOF


#kubectl create -f https://k8s.io/examples/pods/storage/pv-pod.yaml
kubectl create -f ./pv-pod.yaml

kubectl get pod task-pv-pod

kubectl exec -it task-pv-pod -- /bin/bash
apt-get update
apt-get install curl
curl localhost
exit


