package com.example.hapifhir.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

/*
 * @author jyang
 * @date 12/21/2015
 */
public class EncounterResourceProvider implements IResourceProvider {

	@Read()
	public Encounter readEncounterById(@IdParam IdDt theId) {
		String id = theId.getIdPart();
		Encounter encounter = ResourceCache.encMap.get(id);
		if (encounter != null) {
			System.out.println("Read Encounter By Id - Found Encounter with id "	+ encounter.getId().getIdPart());
			
		} else {
			System.out.println("Read Encounter By Id - Not found Encounter with id "+ id);
		}
		return encounter;
	}
		
	
	
	
		
	/**
	 * Note: For reducing the complexity as introduction, this is a simplified implementation that doesn't 
	 *       support resource version history.
	 * @param encounter
	 * @return
	 */
	@Create
	public MethodOutcome createEncounter(@ResourceParam Encounter encounter) {
		String id = null;
		MethodOutcome retVal = new MethodOutcome();
		if (encounter.getId().isEmpty()){
			id = Integer.toString(ResourceCache.nextEncounterID());
			encounter.setId(id);
			ResourceCache.encMap.put(id, encounter);
			retVal.setId(encounter.getId());
			retVal.setCreated(true);
			retVal.setResource(encounter);
			System.out.println("Create Encounter - Created Encounter with id " + encounter.getId().getIdPart());

		}
		else{
			OperationOutcome outcome = new OperationOutcome();
			String msg = "Error: Create Encounter - Should not include Encounter id " + id + " for creating Encounter.";
			outcome.addIssue().setDiagnostics(msg).setSeverity(IssueSeverityEnum.ERROR);
			
			System.out.println(msg);
			retVal.setCreated(false);
			retVal.setOperationOutcome(outcome);
		}
		
		
		return retVal;
	}

	
	@Search
    public List<IResource> searchEncounters() {
        
    	List<IResource> retVal = new ArrayList<IResource>();
    	
    	for (Encounter enc : ResourceCache.encMap.values()){
			retVal.add(enc);
    	}
    	System.out.println("Search All Encounters - Found " + retVal.size() + " Encounter(s).");
    	return retVal;
    }
	
	@Search()
	public List<IResource> searchEncounters(@RequiredParam(name = Encounter.SP_PATIENT+'.'+Patient.SP_RES_ID) TokenParam patientId,
											@IncludeParam() Set<Include> theIncludes){
		final String patId = patientId.getValue();
    	List<IResource> retVal = new ArrayList<IResource>();
    	int size = 0;
    	for (Encounter enc : ResourceCache.encMap.values()){
    		String encPatId = enc.getPatient().getReference().getIdPart();
    		
    		if (patId.equals(encPatId)){
    			retVal.add(enc);
    			size++;
    			String s = enc.getResourceName() + ":" +  Encounter.SP_PATIENT;
    			for (Include inc : theIncludes){
    				System.out.println("Inc=" + inc) ; 
    				if (s.equals(inc.getValue())){
    					Patient pat = ResourceCache.patMap.get(patId);
    					if (pat != null){
    						retVal.add(pat);
    					}
    				}
    			}
    		}
    	}
    	
    	System.out.println("Read Encounters By PatientId - Found " + size + 
    			" Encounter(s) associated with Patient id " + patId);
    	
    	
    	return retVal;
	}
	
	/**
	 * Note: For reducing the complexity as introduction, this is a simplified implementation that doesn't 
	 *       support resource version history.
	 * @param id
	 * @param theEncounter
	 * @return
	 */
	@Update
    public MethodOutcome updateEncounter(@IdParam IdDt id, @ResourceParam Encounter theEncounter) {
		MethodOutcome retVal = new MethodOutcome();
		Encounter enc = readEncounterById(id);
		if (enc == null) {
			OperationOutcome outcome = new OperationOutcome();
			String msg = "Error: Update Encounter - Not found Encounter with id " + id + " in resource store for update.";
			outcome.addIssue().setDiagnostics(msg).setSeverity(IssueSeverityEnum.ERROR);

			System.out.println(msg);
			retVal.setCreated(false);
			retVal.setOperationOutcome(outcome);
		}
		else{

			// merge theEncounter to enc
			Date st = theEncounter.getPeriod().getStart();
			if (st != null) {
				DateTimeDt start = new DateTimeDt(st);
				enc.getPeriod().setStart(start);
			}
			Date ed = theEncounter.getPeriod().getEnd();
			if (ed != null) {
				DateTimeDt end = new DateTimeDt(ed);
				enc.getPeriod().setEnd(end);
			}
			enc.setStatus(theEncounter.getStatusElement());
	
			
			retVal.setId(id);
	
			ResourceCache.persistResource(enc);
	
			System.out.println("Update Encounter - Updated Encounter with id "	+ enc.getId());
		}
		return retVal;
    }

	public Class<? extends IResource> getResourceType() {
		return Encounter.class;
	}

}
