package com.example.hapifhir.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;








import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

/*
 * @author jyang
 * @date 12/21/2015
 */
public class PatientResourceProvider implements IResourceProvider {

    @Read()
    public Patient readPatient(@IdParam IdDt theId) {
        String id = theId.getIdPart();
    	Patient patient = ResourceCache.patMap.get(id);
    	if(patient != null){
    		System.out.println("Read Patient By Id - Found Patient with id " + patient.getId());
    	}
    	else {
    		System.out.println("Read Patient By Id - Not found Patient with id " + theId);
    	}
        return patient;
    }


    @Search()
    public List<Patient> searchPatients(
    		@RequiredParam(name = Patient.SP_FAMILY) StringDt theFamilyName,
    		@OptionalParam(name = Patient.SP_BIRTHDATE) DateParam theDob	) {

        List<Patient> list = new ArrayList<Patient>();

        for (Patient patient : ResourceCache.patMap.values()){
			String ln = patient.getName().get(0).getFamilyAsSingleString();
			Date dob = patient.getBirthDate();
			
			if (ln.equals(theFamilyName.getValue())){
        		if(theDob == null || dob.equals(theDob.getValue())){
        			list.add(patient);
        			System.out.println("Search Patients By Name - Found Patient with id " + patient.getId());
        		}
        	}
        }
        
        return list;
    }

    
    
    @Search
    public List<Patient> searchPatients( @RequiredParam(name = Patient.SP_RES_ID) StringParam theId){
    	String id = theId.getValue();
    	List<Patient> list = new ArrayList<Patient>();

        for (Patient patient : ResourceCache.patMap.values()){
        	String patId = patient.getId().getIdPart();
        	if (id.equals(patId)){
        		list.add(patient);
        		System.out.println("Search Patients By Id - Found Patient with id " + patId);
        	}
        }
        
        return list;
    }
    
    @Search
    public List<Patient> searchPatients() {
        
    	List<Patient> retVal = new ArrayList<Patient>();
    	
    	for (Patient pat : ResourceCache.patMap.values()){
			retVal.add(pat);
    	}
    	System.out.println("Search All Patients - Found " + retVal.size() + " Patient(s).");
    	return retVal;
    }

	public Class<Patient> getResourceType() {
		return Patient.class;
	}

}
