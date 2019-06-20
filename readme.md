
### Certificates Notes 

From opentest email

## keystore - my key
openssl pkcs12 -export -in combined.pem -inkey key.pem -name opentest -out myopentest.p12

keytool -importkeystore -destkeystore keystore.jks -srckeystore myopentest.p12 -srcstoretype PKCS12

## trusted certs store 

Now need to import the servers certificate into the store to trust it

https://stackoverflow.com/questions/32051596/exception-unable-to-validate-certificate-of-the-target-in-spring-mvc

openssl s_client -connect 192.168.128.11:443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > public.crt

(I used the jdk certificate store)
keytool -import -alias SMSPSRVR -keystore cacerts.jks -file public.crt



### Docker 

In this directory

mvn install 

docker build . -t smsp-adaptor

docker tag smsp-adaptor thorlogic/smsp-adaptor

docker push thorlogic/smsp-adaptor

