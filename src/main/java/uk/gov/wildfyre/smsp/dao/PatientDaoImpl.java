package uk.gov.wildfyre.smsp.dao;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.smsp.HapiProperties;
import uk.gov.wildfyre.smsp.support.SpineSecuritySocketFactory;
import javax.xml.soap.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PatientDaoImpl {


    @Autowired
    SpineSecuritySocketFactory spineSecurityContext;

    public Patient read()  {

        return null;
    }



    public List<Patient> search(StringParam family,
                               StringParam given,
                               TokenParam identifier,
                                DateParam dob,
                               TokenParam gender,
                               StringParam postcode) throws Exception {


        try {
            spineSecurityContext.createContext();
        } catch(Exception contextException) {
            throw new InternalErrorException("Context creation: "+contextException.getMessage());
        }
        Socket socket = null;
        SMSPHL7v3ToFHIRTransform smsphl7v3ToFHIRTransform = null;
        try {
            socket = spineSecurityContext.createSocket(HapiProperties.getNhsServerAddress(), 443);


            SMSPSoapRequest soapRequest = new SMSPSoapRequest(family,
                    given,
                    identifier,
                    dob,
                    gender,
                    postcode);

            soapRequest.prepareRequest();

            if (soapRequest.inputStream == null) {
                throw new UnprocessableEntityException("Unable to match query to SMSP Search");
            }

            soapRequest.setOutput(IOUtils
                    .toString(soapRequest.inputStream, StandardCharsets.UTF_8));
            //
            soapRequest.addParameterValues();


            socket = soapRequest.getPrintWriter(socket);
            if (socket == null) {
                throw new InternalErrorException("Unable to open socket");
            }

            soapRequest.buildStringBuilder(socket);


            soapRequest.sendRequest();

            SOAPBody soapBody = soapRequest.soapMessage.getSOAPBody();
            // find your node based on tag name

            smsphl7v3ToFHIRTransform = new SMSPHL7v3ToFHIRTransform(soapBody);

            smsphl7v3ToFHIRTransform.transform();

            soapRequest.out.close();


        }
        catch (IOException ioException) {
                throw new InternalErrorException("Socket creation: "+ioException.getMessage());
            }
        finally {
            if (socket != null)
                socket.close();
        }
        return smsphl7v3ToFHIRTransform.getPatients();

    }

}
