package uk.gov.wildfyre.SMSP.dao;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import com.sun.xml.ws.developer.JAXWSProperties;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.wildfyre.SMSP.HapiProperties;
import uk.gov.wildfyre.SMSP.support.SSLSocketFactoryGenerator;
import uk.gov.wildfyre.SMSP.support.SpineSecuritySocketFactory;
import uk.hscic.itk.pds.FaultResponse;
import uk.hscic.itk.pds.VerifyNHSNumberV10;
import uk.hscic.itk.pds.VerifyNHSNumberV10Ptt;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
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

        spineSecurityContext.createSocket("192.168.128.11", 443);
        /*
        GetPatientDetailsByNHSNumber getPatientDetailsByNHSNumber = new GetPatientDetailsByNHSNumber();

        getPatientDetailsByNHSNumber.callService();
*/
        return null;
    }

    /*

    https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start


     */


    public List<Patient> search(StringParam family,
                                @OptionalParam(name = Patient.SP_GIVEN) StringParam given,
                                @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam identifier,
                                @OptionalParam(name = Patient.SP_BIRTHDATE) DateParam dob,
                                @OptionalParam(name = Patient.SP_GENDER) TokenParam gender,
                                @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringParam postcode) throws Exception {

        List<Patient> patients = new ArrayList<>();
        spineSecurityContext.createContext();

        Socket socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(), 443);

        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getPatientDetailsByNHSNumber.xml");

        PrintWriter
                out = new PrintWriter(
                new BufferedWriter
                        (
                                new OutputStreamWriter(
                                        socket.getOutputStream())));
        String output = IOUtils
                .toString(inputStream, "UTF-8");
        if (dob != null) {
            String date = dob.getValueAsString().replace("-", "");
            log.info(date);
            output = output.replace("__DOB__", date);
        }
        if (identifier != null) {
            log.info(identifier.getValue());
            output = output.replace("__NHSNUMBER__", identifier.getValue());
        }
        log.info(output);
        out.println("POST /smsp/pds HTTP/1.0");
        out.println("Host: fhirsmsp");
        out.println("Content-Length: " + output.length());
        out.println("SOAPAction: urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0");
        out.println("Content-Type: text/xml");

        out.println();
        out.println(output);
        out.flush();


        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader
                in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));


        String inputLine;
        Boolean headers = true;
        while ((inputLine = in.readLine()) != null) {
            log.info(inputLine);
            if (headers) {
                if (inputLine.isEmpty()) {
                    headers = false;
                } else {
                    log.debug(inputLine);
                }
            } else {
                stringBuilder.append(inputLine);
            }
        }

        in.close();

        log.info(stringBuilder.toString());

        InputStream stringStream = new ByteArrayInputStream(stringBuilder.toString()
                .getBytes(Charset
                        .forName("UTF-8")));

        MessageFactory mf = MessageFactory.newInstance();
        // headers for a SOAP message
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stringStream);
        MimeHeaders header = new MimeHeaders();
        header.addHeader("Content-Type", "text/xml");
        SOAPMessage soapMessage = mf.createMessage(header, bufferedInputStream);

        SOAPBody soapBody = soapMessage.getSOAPBody();
        // find your node based on tag name

        NodeList nodes = soapBody.getElementsByTagName("patient");


        if (nodes.getLength() > 0) {

            for (int f = 0; f < nodes.getLength(); f++) {
                Patient patient = new Patient();
                patients.add(patient);
                Node node = nodes.item(f).getFirstChild();
                while (node != null) {
                    switch (node.getNodeName()) {
                        case "id":
                            Node id = node.getAttributes().getNamedItem("extension");
                            if (id != null) patient.setId(id.getNodeValue());
                            Identifier nhsid = patient.addIdentifier();
                            nhsid.setValue(id.getNodeValue()).setSystem("https://fhir.nhs.uk/Id/nhs-number");
                            Extension extension = nhsid.addExtension().setUrl("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-NHSNumberVerificationStatus-1")
                                    .setValue(new CodeableConcept().addCoding(new Coding().setSystem("https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1")
                                            .setCode("01")
                                            .setDisplay("Number present and verified")));

                            break;
                        case "name":
                            NodeList subnode = node.getChildNodes();
                            HumanName name = patient.addName();
                            for (int g = 0; g < subnode.getLength(); g++) {
                                switch (subnode.item(g).getNodeName()) {
                                    case "prefix":
                                        name.addPrefix(subnode.item(g).getTextContent());
                                        break;
                                    case "given":
                                        name.addGiven(subnode.item(g).getTextContent());
                                        break;
                                    case "family":
                                        name.setFamily(subnode.item(g).getTextContent());
                                        break;
                                }
                            }
                            break;
                        case "addr":
                            NodeList adrnode = node.getChildNodes();
                            Address address = patient.addAddress();
                            Node type = node.getAttributes().getNamedItem("use");
                            switch (type.getNodeValue()) {
                                case "H":
                                    address.setUse(Address.AddressUse.HOME);
                                    break;
                            }
                            for (int g = 0; g < adrnode.getLength(); g++) {
                                switch (adrnode.item(g).getNodeName()) {
                                    case "postalCode":
                                        address.setPostalCode(adrnode.item(g).getTextContent());
                                        break;
                                    case "streetAddressLine":
                                        address.addLine(adrnode.item(g).getTextContent());
                                        break;

                                }
                            }
                            break;
                        case "patientPerson":
                            NodeList pernode = node.getChildNodes();

                            for (int g = 0; g < pernode.getLength(); g++) {
                                switch (pernode.item(g).getNodeName()) {
                                    case "administrativeGenderCode":
                                        Node adgender = pernode.item(g).getAttributes().getNamedItem("code");
                                        switch (adgender.getNodeValue()) {
                                            case "1":
                                                patient.setGender(Enumerations.AdministrativeGender.MALE);
                                                break;
                                            case "2":
                                                patient.setGender(Enumerations.AdministrativeGender.FEMALE);
                                                break;
                                            case "9":
                                                patient.setGender(Enumerations.AdministrativeGender.OTHER);
                                                break;
                                            case "X":
                                                patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
                                                break;
                                        }
                                        break;
                                    case "birthTime":
                                        Node dobnode = pernode.item(g).getAttributes().getNamedItem("value");
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                                            patient.setBirthDate(format.parse(dobnode.getNodeValue()));
                                        } catch (Exception e) {

                                        }
                                        break;
                                    case "gPPractice":
                                        if (pernode.item(g).hasChildNodes()) {
                                            NodeList gpnode = pernode.item(g).getChildNodes();
                                            for (int h = 0; h < gpnode.getLength(); h++) {

                                                if (gpnode.item(h).getNodeName().equals("locationOrganization")) {
                                                    NodeList gpsubnode = gpnode.item(h).getChildNodes();

                                                    for (int i = 0; i < gpsubnode.getLength(); i++) {
                                                        switch (gpsubnode.item(i).getNodeName()) {
                                                            case "id":
                                                                Node idnode = gpsubnode.item(i).getAttributes().getNamedItem("extension");
                                                                patient.getManagingOrganization()
                                                                        .getIdentifier()
                                                                        .setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
                                                                        .setValue(idnode.getNodeValue());
                                                                break;
                                                            case "name":
                                                                patient.getManagingOrganization().setDisplay(gpsubnode.item(i).getTextContent());
                                                                break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                    node = node.getNextSibling();
                }
            }
        }
        // check if the node exists and get the value
        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";

        System.out.println(someMsgContent);


        out.close();
        socket.close();
        return patients;

    }

    public MethodOutcome getNHSNumber() throws Exception {


        spineSecurityContext.createContext();

        Socket socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(), 443);


        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getNHSNumber.xml");


        PrintWriter
                out = new PrintWriter(
                new BufferedWriter
                        (
                                new OutputStreamWriter(
                                        socket.getOutputStream())));
        String output = IOUtils
                .toString(inputStream, "UTF-8");

        log.debug(output);
        out.println("POST /smsp/pds HTTP/1.0");
        out.println("Host: fhirsmsp");
        out.println("Content-Length: " + output.length());
        out.println("SOAPAction: urn:nhs-itk:services:201005:getNHSNumber-v1-0");
        out.println("Content-Type: text/xml");

        out.println();
        out.println(output);
        out.flush();


        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader
                in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));


        String inputLine;
        Boolean headers = true;
        while ((inputLine = in.readLine()) != null) {
            if (headers) {
                if (inputLine.isEmpty()) {
                    headers = false;
                } else {
                    log.debug(inputLine);
                }
            } else {
                stringBuilder.append(inputLine);
            }
        }

        in.close();

        log.debug(stringBuilder.toString());

        InputStream stringStream = new ByteArrayInputStream(stringBuilder.toString()
                .getBytes(Charset
                        .forName("UTF-8")));

        MessageFactory mf = MessageFactory.newInstance();
        // headers for a SOAP message
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stringStream);
        MimeHeaders header = new MimeHeaders();
        header.addHeader("Content-Type", "text/xml");
        SOAPMessage soapMessage = mf.createMessage(header, bufferedInputStream);

        SOAPBody soapBody = soapMessage.getSOAPBody();
        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("itk:payload");
        System.out.println("itk:payload = " + nodes.getLength());

        nodes = soapBody.getElementsByTagName("getNHSNumberResponse-v1-0");
        System.out.println("getNHSNumberResponse-v1-0 = " + nodes.getLength());
        // check if the node exists and get the value
        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";

        System.out.println(someMsgContent);


        out.close();
        socket.close();
        return null;

    }


    public MethodOutcome verifyNHSNumber() throws Exception {

        wsVerifyNHSNumber();
        return null;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    public void wsVerifyNHSNumber() throws Exception {


        SSLSocketFactoryGenerator sslSocketFactoryGenerator = new SSLSocketFactoryGenerator("smspsrvr");

        URL wsdlURL = getContextClassLoader().getResource("wsdl/PDSMiniServices-v1-0.wsdl"); //new URL(HapiProperties.getNhsServerUrl());

        VerifyNHSNumberV10 ss = new VerifyNHSNumberV10(wsdlURL, VERIFY_NHS_NUMBER_SERVICE_NAME);

        VerifyNHSNumberV10Ptt port = ss.getVerifyNHSNumberV10PttPort();

        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", spineSecurityContext);
        //bindingProvider.getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, HapiProperties.getNhsServerUrl());
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, HapiProperties.getNhsServerUrl());

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
