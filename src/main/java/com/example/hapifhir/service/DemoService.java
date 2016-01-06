package com.example.hapifhir.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.example.hapifhir.resources.EncounterResourceProvider;
import com.example.hapifhir.resources.PatientResourceProvider;
import com.example.hapifhir.resources.ResourceCache;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;

/*
 * @author jyang
 * @date 12/21/2015
 */

@WebServlet
public class DemoService extends RestfulServer {


	private static final long serialVersionUID = 1L;

	public DemoService() {
		super(FhirContext.forDstu2());
	}
	
    /**
     * The initialize method is automatically called when the servlet is starting up, so it can
     * be used to configure the servlet to define resource providers, or set up
     * configuration, interceptors, etc.
     */
   @Override
   protected void initialize() throws ServletException {
      /*
       * The servlet defines any number of resource providers, and
       * configures itself to use them by calling
       * setResourceProviders()
       */
       
      List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
      resourceProviders.add(new PatientResourceProvider());
      resourceProviders.add(new EncounterResourceProvider());
      setResourceProviders(resourceProviders);
      ResourceCache.buildResourceCache();
      
      System.out.println("InitializeHapiFhirServer - Completed.");
   }

}
