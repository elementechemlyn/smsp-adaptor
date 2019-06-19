package uk.gov.wildfyre.SMSP.dao;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.SMSP.HapiProperties;
import uk.gov.wildfyre.SMSP.support.SpineSecuritySocketFactory;
import uk.hscic.itk.pds.FaultResponse;
import uk.hscic.itk.pds.GetNHSNumberV10;
import uk.hscic.itk.pds.VerifyNHSNumberV10;
import uk.hscic.itk.pds.VerifyNHSNumberV10Ptt;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class PatientDaoImpl {


    @Autowired
    SpineSecuritySocketFactory spineSecurityContext;

    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);

    private static final QName VERIFY_NHS_NUMBER_SERVICE_NAME = new QName("urn:nhs-itk:ns:201005", "verifyNHSNumber-v1-0");



    public Patient read(IdType internalId) throws Exception {

        spineSecurityContext.createContext();

        spineSecurityContext.createSocket("192.168.128.11",443);
        /*
        GetPatientDetailsByNHSNumber getPatientDetailsByNHSNumber = new GetPatientDetailsByNHSNumber();

        getPatientDetailsByNHSNumber.callService();
*/
        return null;
    }

    /*

    https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start


     */


    public List<Patient> search(TokenParam identifier,
                                DateParam dob) throws Exception {

        spineSecurityContext.createContext();

        Socket socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(),443);

        return new ArrayList<>();

    }

    public MethodOutcome verifyNHSNumber( ) throws Exception {

        wsVerifyNHSNumber();
        return null;
    }


    public void wsVerifyNHSNumber() throws Exception {
        URL wsdlURL = VerifyNHSNumberV10.WSDL_LOCATION; //new URL(HapiProperties.getNhsServerUrl());

        VerifyNHSNumberV10 ss = new VerifyNHSNumberV10(wsdlURL, VERIFY_NHS_NUMBER_SERVICE_NAME);

        BindingProvider bindingProvider = (BindingProvider) ss;
        bindingProvider.getRequestContext().put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory",spineSecurityContext);
        VerifyNHSNumberV10Ptt port = ss.getVerifyNHSNumberV10PttPort();

        log.info("Invoking verifyNHSNumberV10...");
        uk.hscic.itk.pds.DistributionEnvelopeType _verifyNHSNumberV10_verifyNHSNumberRequestV10 = null;
        try {
            uk.hscic.itk.pds.DistributionEnvelopeType _verifyNHSNumberV10__return = port.verifyNHSNumberV10(_verifyNHSNumberV10_verifyNHSNumberRequestV10);
            log.info("verifyNHSNumberV10.result=" + _verifyNHSNumberV10__return);

        } catch (FaultResponse e) {
            log.error("Expected exception: faultResponse has occurred.");
            log.error(e.toString());
        }

    }




}
