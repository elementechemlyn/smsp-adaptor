package uk.gov.wildfyre.smsp.dao;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import org.junit.Test;
import org.junit.Assert;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.*;
import uk.gov.wildfyre.smsp.dao.SMSPSoapRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.StringReader;
import org.xml.sax.InputSource;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import uk.gov.wildfyre.smsp.HapiProperties;

public class SMSPSoapRequestTest {

    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
             
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String getDOB(Document doc){
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.DateOfBirth").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        return value.getAttribute("value");
    }

    private String getNHSNumber(Document doc) {
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.NHSNumber").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        return value.getAttribute("extension");   
    }

    private String getGivenName(Document doc) {
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.Name").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        element = (Element)value.getElementsByTagName("given").item(0);
        if (element == null) return null;
        return element.getTextContent();   
    }

    private String getFamilyName(Document doc) {
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.Name").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        element = (Element)value.getElementsByTagName("family").item(0);
        if (element == null) return null;
        return element.getTextContent();   
    }

    private String getPostcode(Document doc) {
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.Postcode").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        return value.getAttribute("code");   
    }

    private String getGender(Document doc) {
        Element element,value;
        element = (Element)doc.getElementsByTagName("Person.Gender").item(0);
        if (element == null) return null;
        value = (Element)element.getElementsByTagName("value").item(0);
        if (value == null) return null;
        return value.getAttribute("code");   
    }

    private void checkMessageId(String id, Document doc) {
        Element element;

        element = (Element)doc.getElementsByTagName("wsa:MessageID").item(0);
        Assert.assertEquals(id,element.getTextContent());
    }

    private void checkTrackingId(String id, Document doc) {
        Element element;

        element = (Element)doc.getElementsByTagName("itk:header").item(0);
        Assert.assertEquals(id,element.getAttribute("trackingid"));
    }
    
    private void checkPayloadId(String id, Document doc) {
        Element element;

        element = (Element)doc.getElementsByTagName("itk:payload").item(0);
        Assert.assertEquals(id,element.getAttribute("id"));
    }
    
    private void checkSOAPValues(Document doc) {
        Element element;

        element = (Element)doc.getElementsByTagName("wsa:To").item(0);
        Assert.assertEquals(HapiProperties.getNhsServerAddress(),element.getTextContent());
        element = (Element)doc.getElementsByTagName("wsa:Address").item(0);
        Assert.assertEquals(HapiProperties.getNhsWsaFrom(),element.getTextContent());
        element = (Element)doc.getElementsByTagName("wsse:Username").item(0);
        Assert.assertEquals(HapiProperties.getNhsWsseUserName(),element.getTextContent());
        element = (Element)doc.getElementsByTagName("itk:id").item(0);
        Assert.assertEquals(HapiProperties.getNhsAuditIdent(),element.getAttribute("uri"));
        Assert.assertEquals(HapiProperties.getNhsAuditIdentType(),element.getAttribute("type"));
        element = (Element)doc.getElementsByTagName("itk:senderAddress").item(0);
        Assert.assertEquals(HapiProperties.getNhsItkSender(),element.getAttribute("uri"));
        element = (Element)doc.getElementsByTagName("itk:manifestitem").item(0);
        String manifestId = element.getAttribute("id");
        element = (Element)doc.getElementsByTagName("itk:payload").item(0);
        String payloadId = element.getAttribute("id");
        Assert.assertEquals(manifestId,payloadId);
    }

    @Test(expected=UnprocessableEntityException.class)
    public void noParameters() {
        SMSPSoapRequest soapRequest = new SMSPSoapRequest(null,null,null,null,null,null);
        soapRequest.prepareRequest();
    }

    @Test(expected=UnprocessableEntityException.class)
    public void justNhsNumber() {
        SMSPSoapRequest soapRequest = new SMSPSoapRequest(null,null,new TokenParam("9990073147"),null,null,null);
        soapRequest.prepareRequest();
    }

    @Test
    public void getPatientDetailsByNHSNumberFullName() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),
                                        new TokenParam("9990073147"),new DateParam("1933-11-14"),null,null);
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertEquals("9990073147",getNHSNumber((doc)));
        Assert.assertEquals("GivenName",getGivenName(doc));
        Assert.assertEquals("FamilyName",getFamilyName(doc));

        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }

    @Test
    public void getPatientDetailsByNHSNumberFamilyName() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),null,
                                        new TokenParam("9990073147"),new DateParam("1933-11-14"),null,null);
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertEquals("9990073147",getNHSNumber(doc));
        Assert.assertNull(getGivenName(doc));
        Assert.assertEquals("FamilyName",getFamilyName(doc));

        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }

    @Test
    public void getPatientDetailsByNHSNumberGivenName() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(null,new StringParam("GivenName"),
                                        new TokenParam("9990073147"),new DateParam("1933-11-14"),null,null);
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertEquals("9990073147",getNHSNumber(doc));
        Assert.assertEquals("GivenName",getGivenName(doc));
        Assert.assertNull(getFamilyName(doc));
        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }

    @Test
    public void getPatientDetailsByNHSNumberNoName() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(null,null,new TokenParam("9990073147"),new DateParam("1933-11-14"),null,null);
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetailsByNHSNumber-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertEquals("9990073147",getNHSNumber(doc));
        Assert.assertNull(getGivenName(doc));
        Assert.assertNull(getFamilyName(doc));

        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }


    @Test
    public void getPatientDetails() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("M"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetails-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertEquals("9990073147",getNHSNumber(doc));


        Assert.assertEquals("GivenName",getGivenName(doc));
        Assert.assertEquals("FamilyName",getFamilyName(doc));
        Assert.assertEquals("DN18 5JD",getPostcode(doc));
        Assert.assertEquals("1",getGender(doc));

        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }

    @Test
    public void getPatientDetailsBySearch() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),null,
            new DateParam("1933-11-14"),new TokenParam("M"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        String output = soapRequest.getOutput();
        Assert.assertEquals(soapRequest.soapAction,"urn:nhs-itk:services:201005:getPatientDetailsBySearch-v1-0");
        Document doc = convertStringToXMLDocument(output);

        Assert.assertEquals("19331114",getDOB(doc));
        Assert.assertNull(getNHSNumber(doc));
        Assert.assertEquals("GivenName",getGivenName(doc));
        Assert.assertEquals("FamilyName",getFamilyName(doc));
        Assert.assertEquals("DN18 5JD",getPostcode(doc));
        Assert.assertEquals("1",getGender(doc));

        this.checkSOAPValues(doc);
        this.checkTrackingId(soapRequest.getTrackingId(), doc);
        this.checkPayloadId(soapRequest.getPayloadId(), doc);
        this.checkMessageId(soapRequest.getMessageId(), doc);
    }
    
    @Test
    public void genderMap() throws Exception {
        SMSPSoapRequest soapRequest;
        String output;
        Document doc;

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("M"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("1",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("male"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("1",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("F"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("2",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("female"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("2",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("O"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("9",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("other"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("9",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("unknown"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("X",this.getGender(doc));

        soapRequest = new SMSPSoapRequest(new StringParam("FamilyName"),new StringParam("GivenName"),new TokenParam("9990073147"),
            new DateParam("1933-11-14"),new TokenParam("U"),new StringParam("DN18 5JD"));
        soapRequest.prepareRequest();
        soapRequest.setOutput(IOUtils.toString(soapRequest.inputStream, StandardCharsets.UTF_8));
        soapRequest.addParameterValues();
        output = soapRequest.getOutput();
        doc = convertStringToXMLDocument(output);
        Assert.assertEquals("X",this.getGender(doc));

    }

    @Test(expected=UnprocessableEntityException.class)
    public void notNHSNumber() throws Exception {

        SMSPSoapRequest soapRequest = new SMSPSoapRequest(null,null,new TokenParam("https://dodgysystem.org/localid","9990073147"),new DateParam("1933-11-14"),null,null);
        soapRequest.prepareRequest();
    }
}