package uk.gov.wildfyre.SMSP.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.wildfyre.SMSP.dao.PatientDaoImpl;

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


    @Read
    public Patient read(HttpServletRequest request, @IdParam IdType internalId) throws Exception {

        return patientDao.read(internalId);

    }

    @Search
    public List<Patient> search(HttpServletRequest request,
                                     @OptionalParam(name = Patient.SP_IDENTIFIER)  TokenParam identifier,
                                     @OptionalParam(name = Patient.SP_FAMILY) StringParam surname,
                                     @OptionalParam(name = Patient.SP_NAME) StringParam name
    ) throws Exception {

        return patientDao.search(identifier, surname, name);
    }


}
