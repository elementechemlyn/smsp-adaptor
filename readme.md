
### Certificates Notes 

From opentest email

https://sslguru.com/knowledgebase.php?action=displayarticle&id=224

combine the certs into a certificate.pem file

openssl x509 -outform der -in certificate.pem -out certificate.der

keytool -import -alias smsp -keystore cacerts.jks -file certificate.der






In this directory

mvn install 

docker build . -t smsp-adaptor

docker tag smsp-adaptor thorlogic/smsp-adaptor

docker push thorlogic/smsp-adaptor

