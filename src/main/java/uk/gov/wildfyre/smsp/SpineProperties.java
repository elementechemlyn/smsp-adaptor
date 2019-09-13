package uk.gov.wildfyre.smsp;

import ca.uhn.fhir.context.ConfigurationException;
import com.google.common.annotations.VisibleForTesting;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class SpineProperties {

    static final String SPINE_PROPERTIES = "spine.properties";

    public static final String USESSLCONTEXT = "org.warlock.http.spine.certs";

    /**
     * System property name for the truststore file.
     */
    public static final String USESSLTRUST = "org.warlock.http.spine.trust";

    /**
     * System property name for the certificate keystore password
     */
    public static final String SSLPASS = "spine.sslcontextpass";

    /**
     * System property name for the trust store password
     */
    public static final String TRUSTPASS = "spine.trustpass";

    /**
     * System property name for the SSL algorithm. May be left un-set, but must
     * be set if the local Java platform's default algorithm is NOT "SunX509".
     */
    public static final String SSLALGORITHM = "sslalgorithm";
    
    private static Properties properties;

    public static String getNhsNumber() {
        return nhsNumber;
    }

    public static void setNhsNumber(String nhsNumber) {
        SpineProperties.nhsNumber = nhsNumber;
    }

    private static String nhsNumber;

    /*
     * Force the configuration to be reloaded
     */
    public static void forceReload() {
        properties = null;
        getProperties();
    }

    /**
     * This is mostly here for unit tests. Use the actual properties file
     * to set values
     */
    @VisibleForTesting
    public static void setProperty(String theKey, String theValue) {
        getProperties().setProperty(theKey, theValue);
    }

    public static Properties getProperties() {
        if (properties == null) {
            // Load the configurable properties file
            try (InputStream in = SpineProperties.class.getClassLoader().getResourceAsStream(SPINE_PROPERTIES)){
                SpineProperties.properties = new Properties();
                SpineProperties.properties.load(in);
            } catch (Exception e) {
                throw new ConfigurationException("Could not load SPINE properties", e);
            }

            Properties overrideProps = loadOverrideProperties();
            if(overrideProps != null) {
                properties.putAll(overrideProps);
            }
        }

        return properties;
    }

    /**
     * If a configuration file path is explicitly specified via -Dspine.properties=<path>, the properties there will
     * be used to override the entries in the default spine.properties file (currently under WEB-INF/classes)
     * @return properties loaded from the explicitly specified configuraiton file if there is one, or null otherwise.
     */
    private static Properties loadOverrideProperties() {
        String confFile = System.getProperty(SPINE_PROPERTIES);
        if(confFile != null) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(confFile));
                return props;
            }
            catch (Exception e) {
                throw new ConfigurationException("Could not load SPINE properties file: " + confFile, e);
            }
        }

        return null;
    }

    private static String getProperty(String propertyName) {
        Properties properties = SpineProperties.getProperties();

        if (properties != null) {
            return properties.getProperty(propertyName);
        }

        return null;
    }

    private static String getProperty(String propertyName, String defaultValue) {
        Properties properties = SpineProperties.getProperties();

        if (properties != null) {
            String value = properties.getProperty(propertyName);

            if (value != null && value.length() > 0) {
                return value;
            }
        }

        return defaultValue;
    }

    private static Boolean getBooleanProperty(String propertyName, Boolean defaultValue) {
        String value = SpineProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    private static Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        String value = SpineProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    public static String getSpineProperties() {
        return SpineProperties.getProperty(SPINE_PROPERTIES);
    }

    public static String getUSESSLCONTEXT() {
        return SpineProperties.getProperty(USESSLCONTEXT);
    }

    public static String getUSESSLTRUST() {
        return SpineProperties.getProperty(USESSLTRUST);
    }

    public static String getSSLPASS() {
        return SpineProperties.getProperty(SSLPASS);
    }

    public static String getTRUSTPASS() {
        return SpineProperties.getProperty(TRUSTPASS);
    }

    public static String getSSLALGORITHM() {
        return SpineProperties.getProperty(SSLALGORITHM);
    }
}
