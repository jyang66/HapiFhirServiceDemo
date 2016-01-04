package com.example.hapifhir.service;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import ca.uhn.fhir.context.FhirContext;
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
      System.out.println("InitializeHapiFhirServer - Completed.");
   }

}
