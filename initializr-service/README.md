Test Chassis Initializr
========================

1. create dockerfile with app
```
  >docker build -t chassis/chassis-initializr --no-cache .
  >docker push chassis/chassis-initializr
```

2. create the pod
```
  >kubectl create -f initializr-k8pod.yaml
  >kubectl get pods -o wide
```  
3. create the corresponding **service**
```
  >kubectl create -f initializr-k8service.yaml
  >kubectl get svc -o wide  
```

4. test the endpoint
 Now you can access your application from outside of Minikube.
 E.g.: http://192.168.99.101:30005/

