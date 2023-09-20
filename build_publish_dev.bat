kubectl scale deployment/omnilink-server-bootstrap --replicas=0 -n nubettix-dev
docker build -t localhost:5000/omnilink.server.bootstrap:latest .
docker push localhost:5000/omnilink.server.bootstrap:latest
kubectl apply -f "bootstrap-server.deployment.yaml"