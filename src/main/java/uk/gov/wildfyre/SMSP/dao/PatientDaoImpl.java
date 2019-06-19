package uk.gov.wildfyre.SMSP.dao;

import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.SMSP.itk.GetNHSNumber;
import uk.gov.wildfyre.SMSP.itk.GetPatientDetailsByNHSNumber;
import uk.gov.wildfyre.SMSP.support.SpineSecurityContext;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component
public class PatientDaoImpl {


    @Autowired
    SpineSecurityContext spineSecurityContext;

    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);



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

    /*
    public List<Patient> search(TokenParam identifier,
                                DateParam dob) throws Exception {
        return new ArrayList<>();

    }

     */

    @Operation(name = "$verifyNHSNumber", idempotent = true, bundleType= BundleTypeEnum.COLLECTION)
    public MethodOutcome getValueCodes(
    ) throws Exception {
        spineSecurityContext.createContext();

        Socket socket = spineSecurityContext.createSocket("192.168.128.11",443);



        return null;
    }

    /*
    @Operation(name = "$nhsNumber", idempotent = true, bundleType= BundleTypeEnum.COLLECTION)
    public MethodOutcome getValueCodes(
            @OperationParam(name="id") TokenParam valueSetId,
            @OperationParam(name="query") ReferenceParam valueSetQuery

    ) throws Exception {
        return null;
    }

     */

}
