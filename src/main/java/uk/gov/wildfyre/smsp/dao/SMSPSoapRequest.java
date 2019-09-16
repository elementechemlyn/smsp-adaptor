package uk.gov.wildfyre.smsp.dao;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SMSPSoapRequest {


    private static final Logger log = LoggerFactory.getLogger(SMSPSoapRequest.class);


    private static final String REPLACE_NAME = "__NAME__";

    private static final String REPLACE_GENDER = "__GENDER__";

    private static final String REPLACE_POSTCODE = "__POSTCODE__";

    private static final String REPLACE_NHSNUMBERSUB = "__NHSNUMBERSUB__";


    public SMSPSoapRequest(StringParam family,
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

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

    public Socket getPrintWriter(Socket socket) {

        try {
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
        }
        catch (IOException ex) {
            return null;
         }
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
