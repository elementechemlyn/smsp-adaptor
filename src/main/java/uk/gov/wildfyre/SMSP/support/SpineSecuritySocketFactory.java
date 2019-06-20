/*
  Copyright 2012 Damian Murphy <murff@warlock.org>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package uk.gov.wildfyre.SMSP.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.wildfyre.SMSP.HapiProperties;
import uk.gov.wildfyre.SMSP.SpineProperties;


import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

/**
 * Class to handle the certificates and signing chains required for Spine messaging.
 * The <code>SpineSecurityContext</code> is also an implementation of a Socket Factory
 * which will issue SSL sockets secured with its certificates, and which are issued
 * with the SSL handshake already completed.
 * 
 * Spine certificates are issued without the signing sub-CA and root-CA certificates, 
 * only the signatures. It has been found during development of these classes that
 * the stock Java 1.6 runtime cannot resolve the signing sub- and root CA certificates
 * when they are present in the trust store only - irrespective of whether they are
 * in the "well known" store in $JRE_HOME/lib/security/cacerts or in a custom keystore,
 * with the result that the signing chain for the endpoint certificate cannot be retrieved.
 * 
 * The Java keystore has been found to be best populated in the following way:
 * <ul>
 * <li>Generate the key pair and certificate signing request (CSR) using OpenSSL</li>
 * <li>Have the CSR signed in the usual way</li>
 * <li>Concatenate the certificate, the Spine sub-CA and the Spine root-CA files in that order</li>
 * <li>Use OpenSSL to make a PKCS#12 file with the private key file, and the concatenated certificates as arguments</li>
 * <li>Use the Java keytool "importkeystore" function to make a Java keystore, with a source store type of "PKCS12"</li>
 * </ul>
 * 
 * Using the resultant keystore file allows the certificate chain to be resolved, and the
 * certificates at both the endpoint and the Spine side to pass the mutual authentication checks.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SpineSecuritySocketFactory
    extends javax.net.SocketFactory
{    
    private static SSLContext context = null;

    private static KeyStore keyStore = null;
    private static KeyStore trustStore = null;

    private boolean ready = false;

    private static final Logger log = LoggerFactory.getLogger(SpineSecuritySocketFactory.class);


    /**
     * Constructor which will get configuration properties from System.properties
     * @throws Exception 
     */
    public SpineSecuritySocketFactory()
            throws Exception
    {
        init();
    }

    public static KeyStore getKeyStore() {
        return keyStore;
    }

    public static KeyStore getTrustStore() {
        return trustStore;
    }

    /**
     * Constructor which will get configuration properties from the given Properties
     * instance.
     * @param p
     * @throws Exception 
     */
    public SpineSecuritySocketFactory(Properties p)
            throws Exception
    {
       
    }

    /**
     * Method to load the trust store, mainly for subclasses - applications should
     * call SpineSecurityContext.init() instead, which calls this, setupKeyStore()
     * and createContext() internally.
     * @throws Exception 
     */
    public void setupTrustStore()
            throws Exception
    {
        log.info("setupTrustStore");
        try {
            String trst = SpineProperties.getUSESSLTRUST();
            if (trst == null) {

                if (SpineProperties.getTRUSTPASS() != null && !SpineProperties.getTRUSTPASS().isEmpty()) {
                    log.info("setupTrustStore - Using cacerts.jks" );
                    trustStore = KeyStore.getInstance("jks");
                    trustStore.load(getResourceAsStream("cacerts.jks"), SpineProperties.getSSLPASS().toCharArray());
                    return;
                } else {
                    log.info("setupTrustStore - No keystore" );
                    return;
                }
            }
            String tp = SpineProperties.getTRUSTPASS();
            if (tp == null) tp = "changeit";
            trustStore = KeyStore.getInstance("jks");
            FileInputStream fis = new FileInputStream(trst);
            if (tp == null) {
                trustStore.load(fis, null);
            } else {
                trustStore.load(fis, tp.toCharArray());
            }
            fis.close();            
        }
        catch (Exception e) {
            System.err.println(e.toString());
            throw e;
        }
    }

    /**
     * Method to load the key store, mainly for subclasses - applications should
     * call SpineSecurityContext.init() instead, which calls this, setupKeyStore()
     * and createContext() internally.
     * @throws Exception 
     */

    public void setupKeyStore() 
            throws Exception
    {
        try {
            String ksf = SpineProperties.getUSESSLCONTEXT();
            String p = SpineProperties.getSSLPASS();
            if (p == null) p = "";
            keyStore = KeyStore.getInstance("jks");
            if (ksf == null || ksf.isEmpty()) {
                keyStore.load(getResourceAsStream("keystore.jks"), SpineProperties.getSSLPASS().toCharArray());
            } else {
                FileInputStream fis = new FileInputStream(ksf);
                keyStore.load(fis, p.toCharArray());
                fis.close();
            }
        }
        catch (Exception e) {
            System.err.println(e.toString());
            throw e;
        }
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    /**
     * Method to create the context, mainly for subclasses - applications should
     * call SpineSecurityContext.init() instead, which calls this, setupKeyStore()
     * and createContext() internally.
     * @throws Exception 
     */    
    public void createContext()
            throws Exception
    {
        try {
            String alg = SpineProperties.getSSLALGORITHM();
            String p = SpineProperties.getSSLPASS();

            KeyManagerFactory kmf = null;
            if (alg == null || alg.isEmpty()) {
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            } else {
                kmf = KeyManagerFactory.getInstance(alg);
            }
            kmf.init(keyStore, p.toCharArray());
            context = SSLContext.getInstance("TLS");            
            if (trustStore == null) {
                context.init(kmf.getKeyManagers(), null, new SecureRandom());            
            } else {
                TrustManagerFactory tmf = null;
                if (HapiProperties.getNhsServerAddress().equals("192.168.128.11")) {

                    // WARNING THIS IS NOT TO BE USED ON REAL LINKS
                    TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
                    };
                    context.init(kmf.getKeyManagers(), trustAllCerts, new SecureRandom());
                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    };
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                    // END OF WARNING
                } else {
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(trustStore);
                    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
                }
            }
            ready = true;
        }
        catch (Exception e) {
            System.err.println(e.toString());
            throw e;
        }
    }
    
    /**
     * Check that everything was initialised correctly. Applications should call
     * this as a sanity check before trying to use the context to create sockets.
     * @return True if everything was set up correctly.
     */
    public boolean isReady() { return ready; }
    
    /**
     * Convenience method to load the key and trust stores, and to initialise
     * the context.
     * @throws Exception 
     */
    public final void init() 
            throws Exception
    {
        setupKeyStore();
        setupTrustStore();
        createContext();
    }
    
    /**
     * Get the client socket factory from the underlying SSL context. Applications
     * SHOULD use the implementations of the createSocket methods that are offered by the
     * SpineSecurityContext itself, directly.
     * @return Client socket factory.
     */
    public SSLSocketFactory getSocketFactory() { return context.getSocketFactory(); }
    
    /**
     * Get the server socket factory from the underlying SSL context.
     * @return Server socket factory
     */
    public SSLServerSocketFactory getServerSocketFactory() { return context.getServerSocketFactory(); }
    
    /**
     * Method to manually add a CA certificate to the internal trust store. 
     * @param certFile
     * @throws Exception 
     */
    public void addCACertificate(String certFile)
            throws Exception
    {
        FileInputStream fis = new FileInputStream(certFile);
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        X509Certificate c = (X509Certificate)cf.generateCertificate(fis);
        fis.close();
        keyStore.setCertificateEntry(c.getSubjectDN().getName(), c);
    }
    
    @Override
    public java.net.Socket createSocket() 
            throws java.io.IOException
    {
        SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket();
        s.startHandshake();
        return s;
    }
    
    @Override
    public java.net.Socket createSocket(String h, int p) 
            throws java.io.IOException, java.net.UnknownHostException
    {
        SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket(h, p);
        s.startHandshake();
        return s;
    }

    @Override
    public java.net.Socket createSocket(String h, int p, java.net.InetAddress la, int lp) 
            throws java.io.IOException, java.net.UnknownHostException
    {
        SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket(h, p, la, lp);
        s.startHandshake();
        return s;
    }

    @Override
    public java.net.Socket createSocket(java.net.InetAddress a, int p) 
            throws java.io.IOException, java.net.UnknownHostException
    {
        SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket(a, p);
        s.startHandshake();
        return s;
    }
   
    @Override
    public java.net.Socket createSocket(java.net.InetAddress a, int p, java.net.InetAddress la, int lp) 
            throws java.io.IOException, java.net.UnknownHostException
    {
        SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket(a, p, la, lp);
        s.startHandshake();
        return s;
    }
    
   public static javax.net.SocketFactory getDefault() {
       try {
        return new SpineSecuritySocketFactory();
       }
       catch (Exception e) {
           e.printStackTrace();
           return null;
       }
   }
}
