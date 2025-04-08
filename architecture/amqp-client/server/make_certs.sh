#!/bin/bash

storepass=${1:-password}
mkdir -p certs/server
mkdir -p certs/client
cd certs

# https://activemq.apache.org/components/classic/documentation/how-do-i-use-ssl
echo "Creating server certificate and keystore"
keytool -genkey -alias broker -keyalg RSA -keystore  server/broker.ks -dname CN=xtuml.org,O=xtuml -storepass ${storepass}
echo "Exporting server certificate"
keytool -export -alias broker -keystore  server/broker.ks -file broker_cert -storepass ${storepass}
echo "Creating client certificate and keystore"
keytool -genkey -alias client -keyalg RSA -keystore client.ks -dname CN=xtuml.org,O=xtuml -storepass ${storepass}
echo "Creating client truststore with server certificate"
keytool -import -noprompt -alias broker -keystore client.ts -file broker_cert -storepass ${storepass}
echo "Exporting client certificate"
keytool -export -alias client -keystore client.ks -file client_cert -storepass ${storepass}
echo "Creating server truststore with client certificate"
keytool -import -noprompt -alias client -keystore server/broker.ts -file client_cert -storepass ${storepass}

echo "Exporting client cert as PEM for openssl"
keytool -importkeystore -srckeystore client.ks  -srcstorepass ${storepass} -deststoretype pkcs12 -destkeystore client.p12 -deststorepass ${storepass} -srcalias client
openssl pkcs12 -passin pass:${storepass} -passout pass:${storepass} -in client.p12 -out client/client.pem

echo "Exporting client ts as PEM for openssl"
keytool -importkeystore -srckeystore client.ts -srcstorepass ${storepass} -deststoretype pkcs12 -destkeystore client_ca.p12 -deststorepass ${storepass}
openssl pkcs12 --passin pass:${storepass} -passout pass:${storepass} -in client_ca.p12 -out client/client_ca.pem

