package com.norbertsram.minnesota.ontology.mapper;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.BeforeClass;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.minnesota.ontology.MinnesotaOntologyReasoner;

public class OntologyTestBase {
	private static InputStream ontology = null;
	private static OntologyHelper helper = null;

	protected static final double MOCK_VALUE_QR_RATIO = 0.35;
	protected static final double MOCK_VALUE_Q_DURATION = 0.035;

	
	protected OntologyHelper getOntologyHelper() {
		if (helper == null) {
			helper = new OntologyHelper(ontology); 
		}
		return helper;
	}


	protected Sample createMockupSample() {
		OntologyHelper helper = getOntologyHelper();
		EcgLead lead = EcgLead.I;
		EcgLeadValue leadValue = new EcgLeadValue(lead);
		EcgProperty property = EcgProperty.INTERVAL_Q;
		leadValue.setValue(property, MOCK_VALUE_Q_DURATION);
		property = EcgProperty.RATIO_QR;
		leadValue.setValue(property, MOCK_VALUE_QR_RATIO);

		Sample sample = new Sample(helper, "testMinnesotaSample", lead);
		sample.addValues(leadValue);
		
		return sample;
	}
	
	@BeforeClass
	public static void loadOntology() throws FileNotFoundException {
		ontology = MinnesotaOntologyReasoner.loadOntology();
	}
}
