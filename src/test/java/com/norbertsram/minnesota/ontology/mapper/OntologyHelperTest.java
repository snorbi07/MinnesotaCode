package com.norbertsram.minnesota.ontology.mapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.norbertsram.flt.variable.FuzzySet;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;
import com.norbertsram.flt.xml.MembershipFunctionBuilder;
import com.norbertsram.flt.xml.Type2FuzzySetBuilder;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.base.Joiner;
import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.PropertyEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.WaveformEntity;
import com.norbertsram.minnesota.rule.RuleModel;

public class OntologyHelperTest extends OntologyTestBase {

	private static final int NUMBER_OF_QDURATION_SUBCLASSES = 6; 
	
	@Test
	public void testBuilderCreation() {
		boolean created = true;
		try {
			getOntologyHelper();
		} catch (Exception ex) {
			created = false;
		}
		assertTrue(created);
	}
	
	@Test 
	public void testWaveformSubClassQuerying() {
		OntologyHelper builder = getOntologyHelper();
		OntologyPathProvider type = WaveformEntity.INTERVAL_Q;
		List<OWLClass> owlClassesForWaveformType = builder.getOwlSubClassesForWaveformType(type);
		assertTrue(owlClassesForWaveformType.size() == NUMBER_OF_QDURATION_SUBCLASSES);
	} 
	
	private <T extends OntologyPathProvider> void testMappingCorrectness(Collection<T> mappingEntities) {
		OntologyHelper builder = getOntologyHelper();
		
		assertTrue(mappingEntities != null);
		assertTrue(!mappingEntities.isEmpty());
		
		Map<OWLClass, T> mappings = new HashMap<>();
		List<T> duplicates = new ArrayList<>();		
		List<T> missingWaveforms = new ArrayList<>();		
		for (T type : mappingEntities) {
			OWLClass owlClass = builder.getOwlClass(type);
			
			boolean isMissing = owlClass == null;
			if (isMissing) {
				missingWaveforms.add(type);
			}
			
			boolean isDuplicate = mappings.containsKey(owlClass);
			if (isDuplicate) {
				duplicates.add(type);
			}
			else {
				mappings.put(owlClass, type);
			}
		}
		
		Joiner joiner = Joiner.on(", ").skipNulls();
		String missingWaveformNames = joiner.join(missingWaveforms);
		boolean hasMissingOrInvalidMappings = !missingWaveforms.isEmpty();
		assertFalse("Missing mapping(s) for: " + missingWaveformNames, hasMissingOrInvalidMappings);
		
		joiner = Joiner.on(", ").skipNulls();
		String duplicateNames = joiner.join(duplicates);
		boolean hasDuplicates = !duplicates.isEmpty();
		assertFalse("Duplicate mapping(s) for: " + duplicateNames, hasDuplicates);
	} 
	
	@Test
	public void testWaveformMappings() {
		WaveformEntity[] values = WaveformEntity.values();
		testMappingCorrectness(Arrays.asList(values));
	}

	@Test
	public void testPropertyMappings() {
		PropertyEntity[] values = PropertyEntity.values();
		testMappingCorrectness(Arrays.asList(values));
	}

	@Test
	public void testRuleMappings() {
		RuleEntity[] values = RuleEntity.values();
		testMappingCorrectness(Arrays.asList(values));
	}
	
	@Test
	public void testWaveformFuzzyClassification() {
		OntologyHelper builder = getOntologyHelper();
		OntologyPathProvider type = WaveformEntity.INTERVAL_Q;
		List<OWLClass> owlClassesForWaveformType = builder.getOwlSubClassesForWaveformType(type);

		for (OWLClass owlClass : owlClassesForWaveformType) {
			OWLAnnotation type1FuzzyValueAnnotation = 
					builder.getType1FuzzyValueAnnotation(owlClass);
			boolean hasType1FuzzyDefinition = type1FuzzyValueAnnotation != null;
			assertTrue("Does not have type-1 fuzzy value!", hasType1FuzzyDefinition);
		
			if (hasType1FuzzyDefinition) {
				OWLAnnotationValue value = type1FuzzyValueAnnotation.getValue();
				OWLLiteral literalValue = (OWLLiteral) value;
				String fuzzySet = literalValue.getLiteral();
                MembershipFunction mf = MembershipFunctionBuilder.build(fuzzySet);
                assertTrue("Has valid/supported fuzzy definition!", mf != null);
			}
            
            OWLAnnotation type2FuzzyValueAnnotation = 
					builder.getType2FuzzyValueAnnotation(owlClass);
			boolean hasType2FuzzyDefinition = type2FuzzyValueAnnotation != null;
			assertTrue("Does not have fuzzy value!", hasType2FuzzyDefinition);
		
			if (hasType2FuzzyDefinition) {
				OWLAnnotationValue value = type2FuzzyValueAnnotation.getValue();
				OWLLiteral literalValue = (OWLLiteral) value;
				String fuzzySet = literalValue.getLiteral();
				Type2FuzzySet<MembershipFunction> type2FuzzySet = Type2FuzzySetBuilder.build(fuzzySet);
				assertTrue("Has valid/supported fuzzy definition!", type2FuzzySet != null);
			}
		}
	}
	
	@Test
	public void testSampleCreation() {
		Sample sample = createMockupSample();
		assertTrue(sample != null);
		List<HasWaveformAxiom> waveforms = sample.getWaveforms();
		assertTrue(!waveforms.isEmpty());
		int numberOfSubclasses = waveforms.size();
		int numberOfExpectedValues = 9; // 2 types of waveforms (Q/R ration, Q duration), 2 + 6 subclasses; 
		assertTrue(numberOfSubclasses == numberOfExpectedValues);
		
		HasWaveformAxiom hasWaveformAxiom = waveforms.get(0);
		Waveform waveform = hasWaveformAxiom.getWaveform();

		Double crispValue = waveform.getCrispValue();
		assertTrue(crispValue == MOCK_VALUE_Q_DURATION); 
		
		OWLObject owlObject = waveform.getOwlObject();
		assertTrue(owlObject != null);
		
		OWLClass waveformClass = waveform.getWaveformClass();
		assertTrue(waveformClass != null);
		
		EcgLead lead = sample.getLead();
		boolean isCorrectLead =EcgLead.I.equals(lead); 
		assertTrue(isCorrectLead);
		
		List<RuleModel> ruleResults = sample.evaluate();
		assertTrue(!ruleResults.isEmpty());
	}
	
	@Test
	public void testOntologyReasoning() {
		OntologyHelper helper = getOntologyHelper();

		List<OWLNamedIndividual> sampleIndividuals = helper.getSampleOwlIndividuals();

		OWLNamedIndividual testSample = sampleIndividuals.get(0);
		List<OWLClass> testSampleInferredTypes = helper.getInferredRuleTypes(testSample);
		
		boolean inferredTypesFound = !testSampleInferredTypes.isEmpty();
		assertTrue(inferredTypesFound);
		
		OWLClass inferredRule = testSampleInferredTypes.get(0);
		List<OWLClass> ruleWaveformTypes = helper.getRuleWaveformTypes(inferredRule);
		assertTrue(!ruleWaveformTypes.isEmpty());
		
		List<OWLNamedIndividual> owlIndividualWaveformValues = helper.getOwlIndividualWaveformValues(testSample);
		List<OWLNamedIndividual> owlIndividualsOfType = helper.getOwlIndividualsOfType(owlIndividualWaveformValues, WaveformEntity.RATIO_QR_UPPER_TRESHOLD);
		assertTrue(owlIndividualsOfType.size() == 1);

		OWLNamedIndividual value = owlIndividualsOfType.get(0);
		OWLLiteral crispValues = helper.getOwlIndividualCrispValue(value);
		assertTrue(crispValues != null);
		
		List<OWLNamedIndividual> ruleLeads = helper.getRuleLeads(inferredRule);
		assertTrue(!ruleLeads.isEmpty());
	}
	
}
