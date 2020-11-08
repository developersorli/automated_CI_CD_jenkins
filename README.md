# cts
CTS - Connectivity Test Server - Ubuntu, Jenkins, Python, Kuebnetes, docker

## Contents

### jenkins-build folder:

It contains all the resources required to build and deploy a Jenkins 
stand-alone pod in Kubernetes.

### jenkins-jobs folder
It contains the groovy Jenkins job definitions for all the Job used to create the CI/CD pipeline.

### service folder
Contains CTS service and docker

### kubernetes folder
Deplojment via kubernetes in test, staging and production
It also contains some placeholder (as SERVICE_NAME, IMAGE_VERSION) 
to be replaced during the pipeline execution with actual build data

# Install development enviroment


## Install docker
```
sudo apt-get update

sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common

sudo apt install curl
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo apt-key fingerprint 0EBFCD88

sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
sudo groupadd docker
sudo usermod -aG docker $USER
docker run hello-world
```

## Install Kubernetes
```
sudo systemctl start docker
sudo systemctl enable docker
sudo apt install apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add
sudo apt-add-repository "deb http://apt.kubernetes.io/ kubernetes-xenial main"
sudo apt install kubeadm kubelet kubectl kubernetes-cni
sudo swapoff -a
sudo nano /etc/fstab
# Comment this line
/swapfile
#to this
#/swapfile

```
# Project

# Checkout project
```
git clone https://github.com/sorli2se/devops.git
```
# Clean existing enviroment
```
sudo apt install net-tools

sudo systemctl status kublet   # see if its actually running
sudo systemctl stop kubelet    # stop it
sudo docker stop $(docker ps -a -q)
sudo docker rm $(docker ps -a -q)
docker container prune
sudo systemctl stop docker
sudo systemctl start docker.service
 sudo kubeadm reset -f && 
 sudo systemctl stop kubelet && 
 sudo systemctl stop docker && 
 sudo rm -rf /var/lib/cni/ && 
 sudo rm -rf /var/lib/kubelet/* && 
 sudo rm -rf /etc/cni/ && 
 sudo ifconfig cni0 down && 
 sudo ifconfig flannel.1 down && 
 sudo ifconfig docker0 down && 
 sudo ip link delete cni0 && 
 sudo ip link delete flannel.1
 sudo rm -rf /var/lib/etcd/*
sudo rm -rf $HOME/.kube

sudo systemctl start docker
sudo systemctl enable docker
sudo systemctl start kubelet
```

# Initialize Kubernetes

```
sudo kubeadm init --pod-network-cidr=10.244.0.0/16
```
## Configure Kubernetes command line

```
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

## Check what's running on the clustert
```
kubectl cluster-info
kubectl get pods -n kube-system
```

## Install Flannel
[https://github.com/coreos/flannel]

Flannel runs a small, single binary agent called flanneld on each host, 
and is responsible for allocating a subnet lease to each host out of a larger, 
preconfigured address space. Flannel uses either the Kubernetes API or 
etcd directly to store the network configuration, the allocated subnets, 
and any auxiliary data (such as the host's public IP).
```
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```
## Find master node name and make it schedulable
```
export K8S_MASTER=$(kubectl get nodes -o name | cut -d/ -f2)
echo $K8S_MASTER

kubectl describe node $K8S_MASTER
kubectl taint node $K8S_MASTER node-role.kubernetes.io/master:NoSchedule-
```
#### Jenkins RBAC Permissions
```
kubectl create -f jenkins-build/rbac.yaml
```

## Find Jenkins secret token
```
sudo apt  install jq
JENKINS_TOKEN=$(kubectl get secrets $(kubectl get sa jenkins -o json|jq -r '.secrets[].name') -o json|jq -r '.data.token'|base64 -d)
echo $JENKINS_TOKEN
```
## Jenkins Build and deploy

```
# Build jenkins docker image
docker build --build-arg K8S_TOKEN=$JENKINS_TOKEN -t jenkins:docker jenkins-build/.

# Deploy Jenkins
kubectl create -f  jenkins-build/deployment.yaml

# Create service
kubectl create -f  jenkins-build/service.yaml
```
## Find Jenkins admin password

```
# Save Jenkins pod name in env var
export JENKINS_POD=$(kubectl get po -l name=jenkins -o name | cut -d/ -f2)
echo $JENKINS_POD

# Get the admin password from the logs 
kubectl logs -f $JENKINS_POD

# Or from inside the container
kubectl exec $JENKINS_POD -- cat /var/jenkins_home/secrets/initialAdminPassword
kubectl exec --stdin --tty $JENKINS_POD -- /bin/bash

kubectl taint node $K8S_MASTER node-role.kubernetes.io/master:NoSchedule-
```
## Configure in jenkins browser

 1.Open url http://localhost:30001
 2.And paste string into textfiled to unlock Jenkins
 3.Click Install suggested plugins
 4.Create first Admin User

##Checkout project job and tool

```
# Get into the jenkins pod
kubectl exec -ti $JENKINS_POD -- bash

 cd /var/jenkins_home/jobs/
 git clone https://github.com/sorli2se/cts-jenkins.git .
```


## Install additional plugins

```
java -jar /var/jenkins_home/war/WEB-INF/jenkins-cli.jar \
    -auth admin:admin \
    -s http://127.0.0.1:8080/ \
    install-plugin copyartifact job-dsl pipeline-utility-steps

java -jar jenkins-cli.jar \
    -auth admin:admin \
    -s http://127.0.0.1:8080/ \
safe-restart
```

Or manually instal plugins: copyartifact job-dsl pipeline-utility-steps

# Deploy project job in Jenkins

In browser click Manage Jenikins -> Reload Coniguration from Disk

Go to
http://localhost:30001:8080/job/kubernetes-config/configure
Source Code Managment > Credetntials > Add
and add credentials of git project

Go to
http://localhost:30001/job/cts-webserver/configure
Source Code Managment > Credetntials > Select130
and add credentials of git project

And go to 
http://localhost:30001/job/kubernetes-config/
And click build

If everthing is ok is last version deployed. 
If we wanna deploy custom version go to
http://localhost:30001/job/cd-job-cts-webserver/
and click build with parameters and select build of CI.

# Auto deploy projets CTS on git commit

Go to 
http://localhost:30001/job/kubernetes-config/configure
and click Poll SCM 
Enter text for example "H/15 * * * *" in field Schedule

Or you can use GitHub hook trigger for GITScm polling

# Test

## Find service end points
```
kubectl describe services -n test | grep 'Endpoints:'
```
Endpoints:         10.244.0.50:8080

## Test connection
```
$curl -i -H GET  'http://10.244.0.50:8080/ping'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 17:58:03 GMT
Content-Type: text/html; charset=utf-8
```

### How to Login in bash console
```
kubectl get pods -n test
kubectl -n test  exec --stdin --tty cts-webserver-c99dc6785-xx2cr -- /bin/bash

```
### Set service in maintance mode
```
kubectl -n test exec cts-webserver-c99dc6785-xx2cr -- touch /opt/app/do_maintance_mode
```
#### Test service in maintance mode
```
curl -i -H GET  'http://10.244.0.50:8080/
curl -i -H GET  'http://10.244.0.50:8080/ping'
```
HTTP/1.0 503 Service Unavalible
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 21:15:08 GMT
Content-Type: text/html; charset=utf-8

### Set service in working mode
```
kubectl -n test exec cts-webserver-c99dc6785-xx2cr -- rm /opt/app/do_maintance_mode
```
#### Test service
```
curl -i -H GET  'http://10.244.0.50:8080/ping'
```
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 21:16:03 GMT
Content-Type: text/html; charset=utf-8

# Infrastructure resilient of failures

optionally specify how much of each resource a Container needs
If the node where a Pod is running has enough of a resource available, 
it's possible (and allowed) for a container to use more resource than 
its request for that resource specifies. However, a container is 
not allowed to use more than its resource limit.
To specify a CPU request for a container, include the resources:requests 
field in the Container resource manifest.
```
    resources:
      requests:
        memory: "64Mi"
        cpu: "250m"
      limits:
        memory: "128Mi"
        cpu: "500m"
    args:
    - -cpus
    - "2"
```
# New bug and how to reproduce bug in Jenkins for buld CD job
If you get error by command:
kubectl --kubeconfig /etc/kubernetes/config create namespace test
error: no matches for kind "Namespace" in version "v1"

The problem is that I login in browser http://ip:port of jenkins end point to Jenikins and not to exposed port.

I have solved this in subject Clean existing enviroment and then 
continue from begining in subject Initialize Kubernetes and repeat till subject Jenkins Build and deploy.

# References

https://github.com/DevOpsPlayground/Hands-on-with-Jenkins-CI-CD-Pipelines-in-Kubernetes
https://www.magalix.com/blog/create-a-ci/cd-pipeline-with-kubernetes-and-jenkins
https://kubernetes.io/docs/tasks/configure-pod-container/assign-cpu-resource/
https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/
