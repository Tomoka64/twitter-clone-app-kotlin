secret-encrypt:
	cd config \
	&& kubesec encrypt --key=gcp:projects/cyberagent-268/locations/global/keyRings/itemae/cryptoKeys/firebase original-secret.yaml > kubesec-secret.yaml

secret-apply:
	cd config \
	&& kubesec decrypt kubesec-secret.yaml | kubectl apply -f -

deploy:
	cd config \
	&& kubectl apply -f deploy.yaml
