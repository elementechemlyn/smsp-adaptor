
# Certificates Notes 


## 1 . keystore - to prove identity on SSL connection

### a. Check certificate works

Re: https://www.ssl.com/how-to/create-a-pfx-p12-certificate-file-using-openssl/

Store the private key into privateKey.key (copy)

Store the certificate (endpoint cert only) into certificate.crt

*openssl pkcs12 -export -out certificate.p12 -inkey privateKey.key -in certificate.crt*

then test with 

following https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start


### b. MAC (Only???)

Convert p12 to PEM (maybe mac OSX only) 

*openssl pkcs12 -in certificate.p12 -out smsp.pem -nodes -clcerts*

then use this to test the certificate (this worked on mac OSX)
Get the getNHSNumber.xml file from information in the SMSP opentest page

*curl -i -X POST -H "SOAPAction: urn:nhs-itk:services:201005:getNHSNumber-v1-0" -H "content-type: text/xml" -E smsp.pem -k https://192.168.128.11/smsp/pds -d @getNHSNumber.xml*

### c. JAVA KeyStore

Once ok, import the p12 file into java keystore

*keytool -importkeystore -destkeystore keystore.jks -srckeystore certificate.p12 -srcstoretype PKCS12*

## 2. trusted certs store - to verify identify of server

Now need to import the servers certificate into the store to trust it

Re: https://stackoverflow.com/questions/32051596/exception-unable-to-validate-certificate-of-the-target-in-spring-mvc

*openssl s_client -connect 192.168.128.11:443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > public.crt*


*keytool -import -alias SMSPSRVR -keystore cacerts.jks -file public.crt*



# Docker Build

In this directory

mvn install 

docker build . -t smsp-adaptor

docker tag smsp-adaptor thorlogic/smsp-adaptor

docker push thorlogic/smsp-adaptor

