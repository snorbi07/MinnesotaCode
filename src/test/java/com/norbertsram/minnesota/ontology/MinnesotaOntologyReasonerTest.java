package com.norbertsram.minnesota.ontology;

import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.ecgapi.model.EcgPatientData;
import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.IntervalType2FuzzySet;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;
import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;
import com.norbertsram.minnesota.rule.RuleResult;

public class MinnesotaOntologyReasonerTest {
	
	@Test
	public void testProcessing() {
		String patientId = "testPatient-123";
		String description = "Fail, you must not!";
		
		EcgPatientData patient = new EcgPatientData(patientId, description);
		EcgLeadValue value = new EcgLeadValue(EcgLead.I);
		value.setValue(EcgProperty.INTERVAL_Q, 0.04);
		value.setValue(EcgProperty.RATIO_QR, 0.34);
		patient.add(value);
		
		Collection<RuleModel> processPatientData = MinnesotaOntologyReasoner.processPatientData(patient);
		assertTrue(processPatientData.size() > 0);
	}
	
	@Test
	public void testInference() {
		RuleModel rule1_1_1 = new RuleModel(RuleEntity.RULE_1_1_1);
		final double crispValue1 = 0.6;
		
		IntervalType2FuzzySet fuzzySet1 = new IntervalType2FuzzySet(new DummyMembershipFunction(0.65), new DummyMembershipFunction(0.5));
		RuleProperty property = new RuleProperty(EcgProperty.RATIO_QR, EcgLead.I, fuzzySet1, crispValue1);
		rule1_1_1.addProperty(property);

		IntervalType2FuzzySet fuzzySet2 = new IntervalType2FuzzySet(new DummyMembershipFunction(0.8), new DummyMembershipFunction(0.7));
		property = new RuleProperty(EcgProperty.RATIO_QR, EcgLead.I, fuzzySet2, 0.75);
		rule1_1_1.addProperty(property);

		RuleResult inferedResult = MinnesotaOntologyReasoner.typeReducedAggregation(rule1_1_1);
		double degreeOfTruth = inferedResult.getDegreeOfTruth();
		
    	DecimalFormat decimalForm = new DecimalFormat("#.####");
    	double roundedVal = Double.valueOf(decimalForm.format(degreeOfTruth));
		assertTrue(roundedVal == 0.5750);
	}
	
	private class DummyMembershipFunction implements MembershipFunction {
		
		private final double value;
		
		public DummyMembershipFunction(double value) {
			this.value = value;
		}
		
		@Override
		public Double valueFor(Double input) {
			return value;
		}
		
	}
	
	
}
