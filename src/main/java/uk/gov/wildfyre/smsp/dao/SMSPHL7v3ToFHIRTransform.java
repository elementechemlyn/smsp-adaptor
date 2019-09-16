package uk.gov.wildfyre.smsp.dao;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.*;
import org.w3c.dom.NodeList;
import javax.xml.soap.SOAPBody;
import java.util.ArrayList;
import java.util.List;

public class SMSPHL7v3ToFHIRTransform {

    NodeList nodes;

    public List<Patient> getPatients() {
        return patients;
    }

    List<Patient> patients;

    public SMSPHL7v3ToFHIRTransform(SOAPBody soapBody) {

        nodes = soapBody.getElementsByTagName("itk:ErrorDiagnosticText");

        if (nodes.getLength()>0) {
            throw new UnprocessableEntityException(nodes.item(0).getTextContent());
        }

        nodes = soapBody.getElementsByTagName("patient");
        if (nodes == null) {

            throw new InternalErrorException("patient node not found in ITK Response");
        }

        patients = new ArrayList<>();

    }

    public void transform() {

        if (nodes.getLength() > 0) {

            for (int f = 0; f < nodes.getLength(); f++) {
                SMSPHL7v3ToFHIRPatientTransform patientTransform = new SMSPHL7v3ToFHIRPatientTransform(nodes.item(f).getFirstChild());
                Patient patient = patientTransform.transform();
                patients.add(patient);
            }
        }
    }


}
