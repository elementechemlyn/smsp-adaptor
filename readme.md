
### Certificates Notes 


## keystore - to prove identity on SSL connection

Re: https://www.ssl.com/how-to/create-a-pfx-p12-certificate-file-using-openssl/

Store the private key into privateKey.key

Store the certificate (only) into certificate.crt

*openssl pkcs12 -export -out certificate.pfx -inkey privateKey.key -in certificate.crt*

then test with 

following https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start

*openssl pkcs12 -in certificate.pfx -out pem_filename.pem -nodes -clcerts*

then use this to test the certificate

curl -i -X POST -H "SOAPAction: urn:nhs-itk:services:201005:getNHSNumber-v1-0" -H "content-type: text/xml" -E pem_filename.pem -k https://192.168.128.11/smsp/pds



*keytool -importkeystore -destkeystore keystore.jks -srckeystore myopentest.p12 -srcstoretype PKCS12*

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

