package com.example.hapifhir.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.instance.model.Enumerations.ResourceType;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Practitioner.PractitionerRole;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierTypeCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.PractitionerRoleEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;

/*
 * @author jyang
 * @date 12/21/2015
 */
public class ResourceCache {

	public static Map<String, Encounter> encMap = new HashMap<String, Encounter>();
	
	public static Map<String, Patient> patMap = new HashMap<String, Patient>();
	
	private static int autoIDEncunter = 100;		

	public static int nextEncounterID(){
		return autoIDEncunter++;
	}
	
	
	
	public static void buildResourceCache() {
		
		try {
			createPatientResource("John", "Maez", "000111111", "1971-09-17", AdministrativeGenderEnum.MALE, "1000", "(451)230-2712");
			createPatientResource("Bill", "Barnett", "000222222", "1963-12-15", AdministrativeGenderEnum.MALE, "1001", "(805)987-1199");
			createPatientResource("Michael", "Blodgett", "000333333", "1942-01-22", AdministrativeGenderEnum.MALE, "1002", "(391)444-0981");
			createPatientResource("Valerie", "Reddington", "000444444", "1957-05-10", AdministrativeGenderEnum.FEMALE, "1003", "(673)781-7100");
			createPatientResource("Angela", "Jeter", "000555555", "1991-03-27", AdministrativeGenderEnum.FEMALE, "1004", "(201)671-3918");
			createPatientResource("Peter", "Jeter", "000666666", "1987-12-14", AdministrativeGenderEnum.MALE, "1005", "(699)425-8811");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createPractitionerResource();
	}
	
	
	
	public static void createPatientResource(String fn, String ln, String ssn, String dob, 
												AdministrativeGenderEnum gender, String id, String phone) throws Exception{
		Patient patient = new Patient();
        
        SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Date dateDOB = fmt.parse(dob);
        DateDt dateDt = new DateDt(dateDOB);
        
        List<ContactPointDt> phones = new ArrayList<ContactPointDt>();
        ContactPointDt phoneDt = new ContactPointDt();
        phoneDt.setValue(phone).setUse(ContactPointUseEnum.MOBILE);
        
        
        patient.setBirthDate(dateDt);
        patient.addName().setUse(NameUseEnum.OFFICIAL).addFamily(ln).addGiven(fn);
        patient.setGender(gender).setTelecom(phones);        
        patient.setId(new IdDt(ResourceType.PATIENT.name(), id, "1"));
        InstantDt d = InstantDt.withCurrentTime();
        ResourceMetadataKeyEnum.UPDATED.put(patient, d);
        
        // Let's add two different types of IDs
        patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setType(IdentifierTypeCodesEnum.TAX)
        	.setSystem("http://hl7.org/fhir/sid/us-ssn").setValue(ssn);
        patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setType(IdentifierTypeCodesEnum.MR)
        	.setSystem("urn:oid:3.1.4.1.5.9.2.6").setValue(id);  
		
        patMap.put(id, patient);
        persistResource(patient);
	}
	
	
	
	public static void createPractitionerResource(){
		Practitioner practitioner = new Practitioner();
		practitioner.addIdentifier().setUse(IdentifierUseEnum.USUAL).setSystem("urn:oid:7.6.5.4.3.2.1.0").setValue("12234567890");
		
		List<AddressDt> addresses = new ArrayList<AddressDt>();
		addresses.add(new AddressDt().setUse(AddressUseEnum.WORK).addLine("201 S. Jackson St.")
							.setCity("Salt Lake City").setPostalCode("84103"));
		
		
		List<Practitioner.PractitionerRole> roles = new ArrayList<Practitioner.PractitionerRole>();
		Practitioner.PractitionerRole role = new PractitionerRole();
		role.setRole(PractitionerRoleEnum.DOCTOR);
		roles.add(role);
		
		practitioner.setGender(AdministrativeGenderEnum.FEMALE)
			.setName(new HumanNameDt().addFamily("Grey").addGiven("Meredith"))
			.setAddress(addresses).setPractitionerRole(roles);
		
		practitioner.setId("12230");
		persistResource(practitioner);
	}
	
	
	
	
	public static void persistResource(BaseResource res){
        FhirContext ctx = new FhirContext();
        String xml = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(res);
		try {
			String path = "/temp/FhirResources";
			new File(path).mkdir();
			String fullpath = String.format("%s/%s.%s.xml", path, res.getClass().getSimpleName(), res.getId().getIdPart());
			FileWriter fw = new java.io.FileWriter(fullpath);
			fw.write(xml);
	        fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
