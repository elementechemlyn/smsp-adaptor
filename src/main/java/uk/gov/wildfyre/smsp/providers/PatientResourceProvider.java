package uk.gov.wildfyre.smsp.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.smsp.dao.PatientDaoImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Component
public class PatientResourceProvider implements IResourceProvider {


    @Autowired
    FhirContext ctx;


    @Autowired
    PatientDaoImpl patientDao;


    private static final Logger log = LoggerFactory.getLogger(PatientResourceProvider.class);

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    // Overview of operations https://developer.nhs.uk/apis/smsp/smsp_getting_started.html

    // Getting started  https://digital.nhs.uk/services/spine/spine-mini-service-provider-for-personal-demographics-service/stage-1-getting-started-quick-start


    /*

    Do we need this??? can just call the other function and get answer back

    @Operation(name = "$verifyNHSNumber", idempotent = true, bundleType= BundleTypeEnum.COLLECTION)
    public MethodOutcome verifyNHSNumber(
        @RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam identifier,
        @RequiredParam(name = Patient.SP_BIRTHDATE) DateParam dob,
        @OptionalParam(name = Patient.SP_FAMILY) StringParam family,
        @OptionalParam(name = Patient.SP_GIVEN) StringParam given
    ) throws Exception {
        return patientDao.verifyNHSNumber();
    }

     */
/*
Use search instead

    @Operation(name = "$getNHSNumber", idempotent = true, bundleType= BundleTypeEnum.COLLECTION)
    public MethodOutcome getNHSNumber(
            @RequiredParam(name = Patient.SP_FAMILY) StringParam family,
            @OptionalParam(name = Patient.SP_GIVEN) StringParam given,
            @RequiredParam(name = Patient.SP_BIRTHDATE) DateParam dob,
            @RequiredParam(name = Patient.SP_GENDER) TokenParam gender,
            @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringParam postcode
            // BLOCKED ON PURPOSE - NOT SAFE @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam localidentifier

    ) throws Exception {
        return patientDao.getNHSNumber();
    }
*/

    // getPatientDetailsByNHSNumber
    // getPatientDetailsBySearch
    // getPatientDetails
    @Search
    public List<Patient> search(HttpServletRequest request,
                                // BLOCKED ON PURPOSE - NOT SAFE @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam localidentifier
                                @OptionalParam(name = Patient.SP_FAMILY) StringParam family,
                                @OptionalParam(name = Patient.SP_GIVEN) StringParam given,
                                @OptionalParam(name = Patient.SP_IDENTIFIER)  TokenParam identifier,
                                @RequiredParam(name = Patient.SP_BIRTHDATE) DateParam dob,
                                @OptionalParam(name = Patient.SP_GENDER) TokenParam gender,
                                @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringParam postcode

    ) throws Exception {

        return patientDao.search(family, given, identifier, dob, gender, postcode);
    }




}