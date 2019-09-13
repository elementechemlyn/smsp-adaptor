package uk.gov.wildfyre.smsp.support;

import uk.gov.wildfyre.smsp.SpineProperties;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

public class SSLSocketFactoryGenerator {


   // https://alesaudate.wordpress.com/2010/08/09/how-to-dynamically-select-a-certificate-alias-when-invoking-web-services/


    private String alias = null;


    public SSLSocketFactoryGenerator (String alias) {
        if (alias == null)
            throw new IllegalArgumentException("The alias may not be null");
        this.alias = alias;

    }


    public SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException {

        KeyManager[] keyManagers = getKeyManagers();
        TrustManager[] trustManagers =getTrustManagers();


        //For each key manager, check if it is a X509KeyManager (because we will override its       //functionality
        for (int i=0; i<keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509KeyManager) {
                keyManagers[i]=new AliasSelectorKeyManager((X509KeyManager)keyManagers[i], alias);
            }
        }


        SSLContext context=SSLContext.getInstance("TLS");
        context.init(keyManagers, trustManagers, null);


        return context.getSocketFactory();

    }




    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }


    private KeyManager[] getKeyManagers()
            throws IOException, GeneralSecurityException
    {

        //Init a key store with the given file.

        String alg=KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);



        KeyStore ks= KeyStore.getInstance("jks");
        ks.load(getResourceAsStream("cacerts.jks"), SpineProperties.getSSLPASS().toCharArray());
        //Init the key manager factory with the loaded key store
        kmFact.init(ks, SpineProperties.getSSLPASS().toCharArray());


        return kmFact.getKeyManagers();

    }


    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException
    {

        String alg=TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);

        KeyStore ks=KeyStore.getInstance("jks");
        ks.load(getResourceAsStream("cacerts.jks"), SpineProperties.getTRUSTPASS().toCharArray());

        tmFact.init(ks);

        return tmFact.getTrustManagers();
    }
}
