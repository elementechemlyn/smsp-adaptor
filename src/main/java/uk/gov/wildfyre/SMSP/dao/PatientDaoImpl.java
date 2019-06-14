package uk.gov.wildfyre.SMSP.dao;

import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class PatientDaoImpl {



    private static final Logger log = LoggerFactory.getLogger(PatientDaoImpl.class);



    public Patient read(IdType internalId) {

        return null;
    }


    public List<Patient> search(TokenParam identifier,
                                StringParam surname, StringParam name) {


        return new ArrayList<>();

    }

}
