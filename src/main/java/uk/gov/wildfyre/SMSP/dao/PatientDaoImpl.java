package uk.gov.wildfyre.SMSP.dao;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.SMSP.itk.GetNHSNumber;
import uk.gov.wildfyre.SMSP.itk.GetPatientDetailsByNHSNumber;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientDaoImpl {



    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);



    public Patient read(IdType internalId) {

        return null;
    }

    /*

    https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start


     */

    public List<Patient> search(TokenParam identifier,
                                DateParam dob) throws Exception {

        GetPatientDetailsByNHSNumber getPatientDetailsByNHSNumber = new GetPatientDetailsByNHSNumber();

        getPatientDetailsByNHSNumber.callService();


        return new ArrayList<>();

    }

}
