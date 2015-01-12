package com.norbertsram.minnesota.ontology.mapper;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.minnesota.rule.RuleProperty;
import com.norbertsram.minnesota.rule.RuleModel;

public class SampleTest extends OntologyTestBase {

	@Test
	public void testDecisionMaking() {
		System.out.println("Executing SampleTest.java...");
		OntologyHelper helper = getOntologyHelper();

		List<OWLNamedIndividual> sampleIndividuals = helper.getSampleOwlIndividuals();
		OWLNamedIndividual testSampleIndividual = sampleIndividuals.get(0);
		Sample testSample = new Sample(helper, "testSample", EcgLead.I, testSampleIndividual);

		List<RuleModel> ruleResults = testSample.evaluate();
		System.out.println("EVALUATION OF SAMPLE: " + testSampleIndividual);
		for (RuleModel ruleResult : ruleResults) {
			String ruleIdentifier = ruleResult.getType().toString();
			System.out.println("\tRULE: " + ruleIdentifier);
			List<RuleProperty> ruleProperties = ruleResult.getProperties();
			for (RuleProperty ruleProperty : ruleProperties) {
				EcgProperty property = ruleProperty.getProperty();
				// TODO: fix
//				double degreeOfTruth = ruleProperty.getDegreeOfTruth();
				System.out.println("\t\tWAVEFORM: " + property);
//				System.out.println("\t\tFUZZY VALUE: " + degreeOfTruth);
			}
		}
		
		
		assertTrue(ruleResults != null);
	}
	
	@Test
	public void testOntologyPopulating() {
		
	}
	
}
