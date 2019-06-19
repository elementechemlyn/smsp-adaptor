package uk.gov.wildfyre.SMSP.dao;

import uk.gov.wildfyre.SMSP.HapiProperties;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;

public class SSLPoke {

    public void test() {
        try {
            SSLSocketFactory var1 = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket var2 = (SSLSocket) var1.createSocket("192.168.128.11", 443);
            InputStream var3 = var2.getInputStream();
            OutputStream var4 = var2.getOutputStream();
            var4.write(1);

            while (var3.available() > 0) {
                System.out.print(var3.read());
            }

            System.out.println("Successfully connected");
            System.exit(0);
        } catch (SSLHandshakeException var5) {
            if (var5.getCause() != null) {
                var5.getCause().printStackTrace();
            } else {
                var5.printStackTrace();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }
}
