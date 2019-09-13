package uk.gov.wildfyre.smsp.dao;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.wildfyre.smsp.HapiProperties;
import uk.gov.wildfyre.smsp.support.SpineSecuritySocketFactory;

import javax.xml.soap.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PatientDaoImpl {


    @Autowired
    SpineSecuritySocketFactory spineSecurityContext;

    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);
    
    private static final String REPLACE_NAME = "__NAME__";

    private static final String REPLACE_GENDER = "__GENDER__";

    private static final String REPLACE_POSTCODE = "__POSTCODE__";

    private static final String REPLACE_NHSNUMBERSUB = "__NHSNUMBERSUB__";

     public Patient read()  {

        return null;
    }



    public List<Patient> search(StringParam family,
                                @OptionalParam(name = Patient.SP_GIVEN) StringParam given,
                                @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam identifier,
                                DateParam dob,
                                @OptionalParam(name = Patient.SP_GENDER) TokenParam gender,
                                @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringParam postcode) throws Exception {

        List<Patient> patients = new ArrayList<>();
        try {
            spineSecurityContext.createContext();
        } catch(Exception contextException) {
            throw new InternalErrorException("Context creation: "+contextException.getMessage());
        }
        Socket socket = null;
        try {
            socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(), 443);
        } catch (IOException ioException) {
            throw new InternalErrorException("Socket creation: "+ioException.getMessage());
        }


        SoapRequest soapRequest = new SoapRequest(family,
                given,
                identifier,
                dob,
                gender,
                postcode);

        soapRequest.prepareRequest();

        if (soapRequest.inputStream == null) {
            throw new UnprocessableEntityException("Unable to match query to SMSP Search");
        }

        soapRequest.output = IOUtils
                .toString(soapRequest.inputStream, StandardCharsets.UTF_8);
        //
        soapRequest.addParameterValues();

        try {
            socket = soapRequest.getPrintWriter(socket);
        } catch (Exception ex) {
            return Collections.emptyList();
        }

        soapRequest.buildStringBuilder(socket);


        soapRequest.sendRequest();

        SOAPBody soapBody = soapRequest.soapMessage.getSOAPBody();
        // find your node based on tag name

        NodeList nodes = soapBody.getElementsByTagName("itk:ErrorDiagnosticText");
        if (nodes.getLength()>0) {
            throw new UnprocessableEntityException(nodes.item(0).getTextContent());
        }

        nodes = soapBody.getElementsByTagName("patient");


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
                            nhsid.addExtension().setUrl("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-NHSNumberVerificationStatus-1")
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
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "addr":
                            NodeList adrnode = node.getChildNodes();
                            Address address = patient.addAddress();
                            Node type = node.getAttributes().getNamedItem("use");
                            switch (type.getNodeValue()) {
                                case "HP":
                                case "H":
                                    address.setUse(Address.AddressUse.HOME);
                                    break;
                                case "WP":
                                    address.setUse(Address.AddressUse.WORK);
                                    break;
                                case "TMP":
                                    address.setUse(Address.AddressUse.HOME);
                                    break;
                                case "PST" :
                                    address.setUse(Address.AddressUse.NULL);
                                    address.setType(Address.AddressType.POSTAL);
                                    break;
                                default:
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
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "telecom":
                            ContactPoint contact = patient.addTelecom();
                            Node use = node.getAttributes().getNamedItem("use");
                            if (use != null) {
                                switch (node.getNodeValue()) {
                                    case "H" :
                                    case "HP" :
                                        contact.setUse(ContactPoint.ContactPointUse.HOME);
                                    break;
                                    case "WP" :
                                        contact.setUse(ContactPoint.ContactPointUse.WORK);
                                        break;
                                    case "MC" :
                                        contact.setUse(ContactPoint.ContactPointUse.MOBILE);
                                        break;
                                    case "PG" :
                                        contact.setSystem(ContactPoint.ContactPointSystem.PAGER);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            Node value = node.getAttributes().getNamedItem("value");
                            if (value != null) {
                                contact.setValue(value.getNodeValue());
                                if (value.getNodeValue().contains("fax")) {
                                    contact.setSystem(ContactPoint.ContactPointSystem.FAX);
                                }
                                if (value.getNodeValue().contains("tel")) {
                                    contact.setSystem(ContactPoint.ContactPointSystem.PHONE);
                                }
                                if (value.getNodeValue().contains("@")) {
                                    contact.setSystem(ContactPoint.ContactPointSystem.EMAIL);
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
                                            default:
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
                                                        if (gpsubnode.item(i).getNodeName().equals("id")) {
                                                            Node idnode = gpsubnode.item(i).getAttributes().getNamedItem("extension");
                                                            patient.getManagingOrganization()
                                                                    .getIdentifier()
                                                                    .setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
                                                                    .setValue(idnode.getNodeValue());
                                                        }
                                                        if (gpsubnode.item(i).getNodeName().equals("name")) {

                                                            patient.getManagingOrganization().setDisplay(gpsubnode.item(i).getTextContent());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        default:
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

        log.debug(someMsgContent);


        soapRequest.out.close();
        socket.close();
        return patients;

    }

    public MethodOutcome getNHSNumber() throws Exception {


        spineSecurityContext.createContext();

        Socket socket;
      //  try {
            socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(), 443);
      //  } catch (Exception ex) {
      //      throw new InternalErrorException("Socket creation: "+ ex.getMessage());
      //  }


        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getNHSNumber.xml");


        PrintWriter
                out = new PrintWriter(
                new BufferedWriter
                        (
                                new OutputStreamWriter(
                                        socket.getOutputStream())));
        String output = IOUtils
                .toString(inputStream, StandardCharsets.UTF_8);

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
        boolean headers = true;
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

        log.debug("{}",stringBuilder);

        InputStream stringStream = new ByteArrayInputStream(stringBuilder.toString()
                .getBytes(StandardCharsets.UTF_8));

        MessageFactory mf = MessageFactory.newInstance();
        // headers for a SOAP message
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stringStream);
        MimeHeaders header = new MimeHeaders();
        header.addHeader("Content-Type", "text/xml");
        SOAPMessage soapMessage = mf.createMessage(header, bufferedInputStream);

        SOAPBody soapBody = soapMessage.getSOAPBody();
        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("itk:payload");
        log.trace("itk:payload = {}", nodes.getLength());

        nodes = soapBody.getElementsByTagName("getNHSNumberResponse-v1-0");
        log.trace("getNHSNumberResponse-v1-0 = {}", nodes.getLength());
        // check if the node exists and get the value
        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";

        log.debug(someMsgContent);


        out.close();
        socket.close();
        return null;

    }



    private class SoapRequest {
        public SoapRequest(StringParam family,
                           StringParam given,
                           TokenParam identifier,
                           DateParam dob,
                           TokenParam gender,
                           StringParam postcode) {
            this.dob = dob;
            this.family = family;
            this.given = given;
            this.identifier = identifier;
            this.gender = gender;
            this.postcode = postcode;
        }

        InputStream inputStream = null;

        StringBuilder stringBuilder;
        SOAPMessage soapMessage;

        String soapAction = null;

        PrintWriter
                out;

        StringParam family;
        StringParam given;
        TokenParam identifier;
        DateParam dob;
        TokenParam gender;
        StringParam postcode;
        boolean addNHSNumber = false;

        private String output;

        public void prepareRequest() {
            this.inputStream = null;

            if (identifier != null && postcode == null) {
                if (identifier.getSystem() != null && (!identifier.getSystem().equals("https://fhir.nhs.uk/Id/nhs-number"))) {
                    throw new UnprocessableEntityException("Only NHS Number searches are supported.");
                }
                if (dob == null) {
                    throw new UnprocessableEntityException("Date of Birth must be supplied with identifier searches");
                }
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getPatientDetailsByNHSNumber.xml");
                soapAction = "urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0";
            } else {
                processRequestDemographics();
            }
        }

        private void processRequestDemographics() {
            if (family == null) {
                if (gender == null && given == null && postcode == null) {
                    throw new UnprocessableEntityException("Can not search on just the date of birth");
                } else {
                    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getPatientDetails.xml");
                    soapAction = "urn:nhs-itk:services:201005:getPatientDetails-v1-0";
                    if (identifier != null) addNHSNumber = true;
                }
            } else {
                if (gender == null) {
                    throw new UnprocessableEntityException("Gender must be supplied with date of birth and surname search");
                } else {
                    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("smsp/getPatientDetailsBySearch.xml");
                    soapAction = "urn:nhs-itk:services:201005:getPatientDetailsBySearch-v1-0";
                }
            }
        }

        public void addParameterValues() {

            if ((family != null) || given != null) {
                if (given == null) {
                    output = output.replace(REPLACE_NAME, "<Person.Name><value><family>__FAMILY__</family></value><semanticsText>Person.Name</semanticsText></Person.Name>");
                } else if (family == null) {
                    output = output.replace(REPLACE_NAME, "<Person.Name><value><given>__GIVEN__</given></value><semanticsText>Person.Name</semanticsText></Person.Name>");
                } else {
                    output = output.replace(REPLACE_NAME, "<Person.Name><value><given>__GIVEN__</given><family>__FAMILY__</family></value><semanticsText>Person.Name</semanticsText></Person.Name>");
                }
            } else {
                output = output.replace(REPLACE_NAME, "");
            }

            if (gender != null) {
                output = output.replace(REPLACE_GENDER, "<Person.Gender><value code=\"__GENDER__\" codeSystem=\"2.16.840.1.113883.2.1.3.2.4.16.25\"/><semanticsText>Person.Gender</semanticsText></Person.Gender>");
            } else {
                output = output.replace(REPLACE_GENDER, "");
            }
            if (postcode != null) {
                output = output.replace(REPLACE_POSTCODE, "<Person.Postcode><value code=\"__POSTCODE__\" /><semanticsText>Person.Postcode</semanticsText></Person.Postcode>");
            } else {
                output = output.replace(REPLACE_POSTCODE, "");
            }
            if (addNHSNumber) {
                output = output.replace(REPLACE_NHSNUMBERSUB, "<Person.NHSNumber><value root=\"2.16.840.1.113883.2.1.4.1\" extension=\"__NHSNUMBER__\"/><semanticsText>Person.NHSNumber</semanticsText></Person.NHSNumber>");
            } else {
                output = output.replace(REPLACE_NHSNUMBERSUB, "");
            }
            addExtraValues();
        }

        public void addExtraValues() {

            // Complete search fields
            if (dob != null) {
                String date = dob.getValueAsString().replace("-", "");

                output = output.replace("__DOB__", date);
            }
            if (identifier != null) {
                log.trace("identifier = {}", identifier.getValue());
                output = output.replace("__NHSNUMBER__", identifier.getValue());
            }
            if (gender != null) {
                switch (gender.getValue()) {
                    case "M":
                    case "male":
                        output = output.replace(REPLACE_GENDER, "1");
                        break;
                    case "F":
                    case "female":
                        output = output.replace(REPLACE_GENDER, "2");
                        break;
                    case "O":
                    case "other":
                        output = output.replace(REPLACE_GENDER, "9");
                        break;
                    case "U":
                    case "unknown":
                        output = output.replace(REPLACE_GENDER, "X");
                        break;
                    default:
                        break;
                }
            }
            if (family != null) {
                output = output.replace("__FAMILY__", family.getValue());
            }
            if (given != null) {
                output = output.replace("__GIVEN__", given.getValue());
            }
            if (postcode != null) {
                output = output.replace(REPLACE_POSTCODE, postcode.getValue());
            }
            log.debug(output);
        }

        public Socket getPrintWriter(Socket socket) throws Exception {
            out = new PrintWriter(
                    new BufferedWriter
                            (
                                    new OutputStreamWriter(
                                            socket.getOutputStream())));

            out.println("POST /smsp/pds HTTP/1.0");
            out.println("Host: fhirsmsp");
            out.println("Content-Length: " + output.length());
            out.println("SOAPAction: " + soapAction);
            out.println("Content-Type: text/xml");

            out.println();
            out.println(output);
            out.flush();
            return socket;
        }

        public void sendRequest() throws SOAPException, IOException {
            InputStream stringStream = new ByteArrayInputStream(stringBuilder.toString()
                    .getBytes(StandardCharsets.UTF_8));

            MessageFactory mf = MessageFactory.newInstance();
            // headers for a SOAP message
            BufferedInputStream bufferedInputStream = new BufferedInputStream(stringStream);
            MimeHeaders header = new MimeHeaders();
            header.addHeader("Content-Type", "text/xml");
            soapMessage = mf.createMessage(header, bufferedInputStream);
        }

        public void buildStringBuilder(Socket socket) throws IOException {

            BufferedReader in;

                in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));

                stringBuilder = new StringBuilder();
                String inputLine;
                boolean headers = true;
                while ((inputLine = in.readLine()) != null) {
                    log.trace(inputLine);
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
                log.trace("{}", stringBuilder);
                in.close();

        }
    }
}
