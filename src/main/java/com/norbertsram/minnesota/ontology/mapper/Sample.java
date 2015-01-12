package com.norbertsram.minnesota.ontology.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;
import com.norbertsram.flt.xml.Type2FuzzySetBuilder;
import com.norbertsram.minnesota.common.Utility;
import com.norbertsram.minnesota.ontology.mapper.entity.GroupEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.PropertyEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.WaveformEntity;
import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;

public class Sample implements OwlRepresentation<OWLNamedIndividual> {
	
	private static final Logger LOG = 
			LoggerFactory.getLogger(Sample.class);
	
	private final OWLNamedIndividual owlIndividual;
	private final String identifier;
	private final OntologyHelper helper;
	private final EcgLead lead;
	private final OWLIndividual leadIndividual;
	private List<HasWaveformAxiom> waveforms;
	
	Sample(OntologyHelper helper, String identifier, EcgLead lead) {
		this(helper, identifier, lead, null);
	}
	
	Sample(OntologyHelper helper, String identifier, EcgLead lead, OWLNamedIndividual individual) {
		this.helper = Objects.requireNonNull(helper);
		this.identifier = Objects.requireNonNull(identifier);
		this.lead = Objects.requireNonNull(lead);
		this.leadIndividual = getOwlLeadIndividual(this.lead);
		if (individual == null) { 
			this.owlIndividual = createOntologyRepresentation();
		}
		else {
			this.owlIndividual = individual;
		}
		waveforms = new ArrayList<>();
	}
	
	private OWLNamedIndividual createOntologyRepresentation() {
		assert helper != null;
		OWLNamedIndividual individual = helper.createOwlIndividual(identifier);
		
		OWLClass owlType = helper.getOwlClass(GroupEntity.SAMPLE);
		OWLClassAssertionAxiom hasType = 
				helper.getFactory().getOWLClassAssertionAxiom(owlType, individual);
		helper.addAxiomToOntology(hasType);
		
		OWLObjectProperty hasLead = helper.getOwlObjectProperty(PropertyEntity.HAS_LEAD);
		OWLObjectPropertyAssertionAxiom hasLeadObjectPropertyAssertionAxiom = 
				helper.getFactory().getOWLObjectPropertyAssertionAxiom(hasLead, individual, leadIndividual);
		helper.addAxiomToOntology(hasLeadObjectPropertyAssertionAxiom);
		
		return individual;
	}
	
	private OWLIndividual getOwlLeadIndividual(EcgLead lead) {
		Objects.requireNonNull(lead);
		OntologyPathProvider mapping = EcgPropertyMapper.getOntologyMapping(lead);
		OWLNamedIndividual leadIndividual = helper.getOwlIndividual(mapping);
		
		return leadIndividual;
	}
	
	public boolean addValues(EcgLeadValue leadData) {
		boolean result = false;
		
		if (leadData != null) {
			EcgLead givenLead = leadData.getLead();
			boolean isCorrespondingLeadValue = lead.equals(givenLead); 
			if (!isCorrespondingLeadValue) {
				throw new IllegalArgumentException("Expected data for lead '" + lead + "', received data for lead '" + givenLead + "'!");
			}
			
			List<EcgProperty> ecgProperties = leadData.getEcgProperties();
			for (EcgProperty property : ecgProperties) {
				Double waveformValue = leadData.getValue(property);
				result |= addValue(property,  waveformValue);
			}
		}

		return result;
	}
	
	private boolean addValue(EcgProperty property, Double waveformValue) {
		boolean hasProcessableValue = !Double.isNaN(waveformValue);
		
		OntologyPathProvider mapping = EcgPropertyMapper.getOntologyMapping(property);
		boolean isSupportedProperty = mapping != EcgPropertyMapper.UNSUPPORTED_MAPPING;

		// Populate the ontology with the corresponding waveform individuals.
		// An ECG waveform is represented by one or more OWL Classes, where each class stands for a state of the ECG waveform (membership function)
		boolean doAdd = hasProcessableValue && isSupportedProperty; 
		if (doAdd) {
			List<OWLClass> waveformTypes = getWaveformTypes(mapping);
			for (OWLClass subtype : waveformTypes) {
				addWaveformSubClass(property, subtype, waveformValue);
			}
		}
		
		return doAdd;
	}
	
	public List<HasWaveformAxiom> getWaveforms() {
		return Collections.unmodifiableList(waveforms);
	}
	
	private void addWaveformSubClass(EcgProperty property, OWLClass waveformSubType, Double waveformValue) {
		String waveformIdentifier = getWaveformIdentifier(property, waveformSubType, waveformValue);
		Waveform waveform = new Waveform(helper, waveformSubType, waveformIdentifier);
		assert !Double.isNaN(waveformValue);
		waveform.setCrispValue(waveformValue);
		HasWaveformAxiom hasWaveform = new HasWaveformAxiom(helper, this, waveform);
		waveforms.add(hasWaveform);
	}

	private String getWaveformIdentifier(EcgProperty property, OWLClass waveformSubType, Double value) {
		String identifier = property.getPropertyKey() + hashCode() + waveformSubType.hashCode() + ":" + value;
		return identifier;
	}

	private List<OWLClass> getWaveformTypes(OntologyPathProvider mapping) {
		List<OWLClass> subClasses = Collections.emptyList();
		
		if (mapping != null) {
			subClasses = helper.getOwlSubClassesForWaveformType(mapping);
		}
		
		return subClasses;
	}
	
	public List<RuleModel> evaluate() {
		helper.getReasoner().flush(); //in case the ontology has been modified, we'll do a flush before starting the diagnostic reasoning
		OWLNamedIndividual thisIndividual = getOwlObject();
		List<OWLClass> inferredTypes = helper.getInferredRuleTypes(thisIndividual);
		
		List<RuleModel> rules = Collections.emptyList();
		boolean inferredTypesFound = !inferredTypes.isEmpty();
		if (inferredTypesFound) {
			rules = new ArrayList<>();
			for (OWLClass rule : inferredTypes) {
				RuleModel ruleResult = getRuleResult(rule);
				rules.add(ruleResult);
			}
		}
		
		return rules;
	}
	
	private RuleModel getRuleResult(OWLClass ruleOntologyRepresentation) {
//		String ruleClassIdentifier = getOwlClassIdentifier(ruleOntologyRepresentation);
		String ruleFullPath = ruleOntologyRepresentation.toStringID();
		RuleEntity ruleEntityForPath = RuleEntity.getRuleEntityForPath(ruleFullPath);
		RuleModel ruleResult = new RuleModel(ruleEntityForPath);
		
		Map<OWLClass, RuleEvaluationStep> fuzzyValuesForInferredRule = 
				getFuzzyValuesForInferredRule(ruleOntologyRepresentation);
		for (Entry<OWLClass, RuleEvaluationStep> property : fuzzyValuesForInferredRule.entrySet()) {

			OWLClass ecgPropertyOwlRepresentation = property.getKey();
			OWLClass waveformFamily = helper.getWaveformFamilyForSubtype(ecgPropertyOwlRepresentation);
			String waveformFamilyPath = waveformFamily.toStringID();
			WaveformEntity waveformEntityForPath = WaveformEntity.getWaveformEntityForPath(waveformFamilyPath);
			EcgProperty ecgProperty = EcgPropertyMapper.getEcgProperty(waveformEntityForPath);

			RuleEvaluationStep ruleStep = property.getValue();
			
			RuleProperty ruleProperty = new RuleProperty(ecgProperty, ruleStep.getType2FuzzySet(), ruleStep.getCrispValue());
			ruleResult.addProperty(ruleProperty);
		}
		
		return ruleResult;
	}

	private Map<OWLClass, OWLNamedIndividual> getWaveformOwlIndividualsForRule(OWLClass rule) {
		Map<OWLClass, OWLNamedIndividual> result = new HashMap<>();
		OWLNamedIndividual thisIndividual = getOwlObject();
		
		List<OWLClass> ruleWaveformTypes = helper.getRuleWaveformTypes(rule);
		List<OWLNamedIndividual> owlIndividualWaveformValues = 
				helper.getOwlIndividualWaveformValues(thisIndividual);

		for (OWLClass waveformType : ruleWaveformTypes) {
			List<OWLNamedIndividual> owlIndividualsOfType = 
					helper.getOwlIndividualsOfType(owlIndividualWaveformValues, waveformType);
			
			int numValues = owlIndividualsOfType.size(); 
			if (numValues > 1) {
				throw new IllegalStateException("Sample has '" + numValues + "' of waveform type '" + waveformType + "'!");
			}
			if (owlIndividualsOfType.isEmpty()) {
				throw new IllegalStateException("Sample has no value defined for type '" + waveformType + "'!");
			}
			
			OWLNamedIndividual waveformIndividual = owlIndividualsOfType.iterator().next();
			
			result.put(waveformType, waveformIndividual);
		}
		
		return result;
	}
	
	private Double parseLiteral(OWLLiteral literal) {
		Objects.requireNonNull(literal);
		
		String rawValue = literal.getLiteral();
		double value = Utility.parseValue(rawValue);
		
		return value;
	}
	
	private Map<OWLClass, Double> getWaveformCrispValues(Map<OWLClass, OWLNamedIndividual> waveformOwlIndividualsForRule) {
		Map<OWLClass, Double> crispValues = new HashMap<>();

		for (Entry<OWLClass,OWLNamedIndividual> element : waveformOwlIndividualsForRule.entrySet()) {
			OWLNamedIndividual value = element.getValue();
			OWLClass waveformType = element.getKey();

			OWLLiteral crispValueLiteral = helper.getOwlIndividualCrispValue(value);
			Double crispValue = null;
			boolean hasCrispValue = crispValueLiteral != null;
			if (hasCrispValue) {
				crispValue = parseLiteral(crispValueLiteral);
			}
			else {
				LOG.error("Crisp value entry not defined for type '{}' in individual {}!", waveformType, value);
			}
			
			crispValues.put(waveformType, crispValue);
		}
		
		return crispValues;
	}
	
	private Map<OWLClass, Type2FuzzySet<MembershipFunction>> getFuzzySetDefintionsForWaveforms(List<OWLClass> ruleWaveformTypes) {
		Map<OWLClass, Type2FuzzySet<MembershipFunction>> result = new HashMap<>();
		
		for (OWLClass owlClass : ruleWaveformTypes) {
			OWLAnnotation fuzzyValueAnnotation = helper.getFuzzyValueAnnotation(owlClass);
			
			Type2FuzzySet<MembershipFunction> fs = null;
			if (fuzzyValueAnnotation != null) {
				OWLAnnotationValue value = fuzzyValueAnnotation.getValue();
				OWLLiteral literalValue = (OWLLiteral) value; 
				String fuzzySet = literalValue.getLiteral();
				fs = Type2FuzzySetBuilder.build(fuzzySet);
			}
			else {
				LOG.error("Fuzzy set definition not found for type '{}'", owlClass);
			}
			result.put(owlClass, fs);
		}
		
		return result;
	}

//	@Deprecated
//	private Map<OWLClass, MembershipFunction> getFuzzyDefintionsForWaveforms(List<OWLClass> ruleWaveformTypes) {
//		Map<OWLClass, MembershipFunction> result = new HashMap<>();
//		
//		for (OWLClass owlClass : ruleWaveformTypes) {
//			OWLAnnotation fuzzyValueAnnotation = helper.getFuzzyValueAnnotation(owlClass);
//			
//			MembershipFunction mf = null;
//			if (fuzzyValueAnnotation != null) {
//				OWLAnnotationValue value = fuzzyValueAnnotation.getValue();
//				OWLLiteral literalValue = (OWLLiteral) value; 
//				String membershipFunction = literalValue.getLiteral();
//				mf = MembershipFunctionBuilder.build(membershipFunction);
//			}
//			else {
//				LOG.error("Fuzzy definition not found for type '{}'", owlClass);
//			}
//			result.put(owlClass, mf);
//		}
//		
//		return result;
//	}
	
	private Map<OWLClass, RuleEvaluationStep> getFuzzySetValues(List<OWLClass> ruleWaveformTypes, 
			Map<OWLClass, Double> waveformCrispValues, 
			Map<OWLClass, Type2FuzzySet<MembershipFunction>> fuzzyDefintionsForWaveforms) {

		Map<OWLClass, RuleEvaluationStep> fuzzyStubs = new HashMap<>();

		for (OWLClass waveformType : ruleWaveformTypes) {
			Double crispValue = waveformCrispValues.get(waveformType);
			boolean isCrispValueAvailable = crispValue != null;
			if (!isCrispValueAvailable) {
				LOG.error("Missing crisp value for type '{}'!", waveformType);
			}
			
			Type2FuzzySet<MembershipFunction> type2FuzzySet = fuzzyDefintionsForWaveforms.get(waveformType);
			boolean isFuzzySetAvailable = type2FuzzySet != null; 
			if (!isFuzzySetAvailable) {
				LOG.error("Missing fuzzy definition for type '{}'!", waveformType);
			}

			if (isCrispValueAvailable && isFuzzySetAvailable)	{
				RuleEvaluationStep stub = new RuleEvaluationStep(type2FuzzySet, crispValue);
				fuzzyStubs.put(waveformType, stub);
			}
		}

		return fuzzyStubs;
	}

//	@Deprecated
//	private Map<OWLClass, RuleEvaluationStep> getFuzzyValues(List<OWLClass> ruleWaveformTypes, 
//			Map<OWLClass, Double> waveformCrispValues, 
//			Map<OWLClass, MembershipFunction> fuzzyDefintionsForWaveforms) {
//		
//		Map<OWLClass, RuleEvaluationStep> fuzzyStubs = new HashMap<>();
//		
//		for (OWLClass waveformType : ruleWaveformTypes) {
//			Double crispValue = waveformCrispValues.get(waveformType);
//			boolean isCrispValueAvailable = crispValue != null;
//			if (!isCrispValueAvailable) {
//				LOG.error("Missing crisp value for type '{}'!", waveformType);
//			}
//			
//			MembershipFunction membershipFunction = fuzzyDefintionsForWaveforms.get(waveformType);
//			boolean isMembershipFunctionAvailable = membershipFunction != null; 
//			if (!isMembershipFunctionAvailable) {
//				LOG.error("Missing fuzzy definition for type '{}'!", waveformType);
//			}
//			
//			if (isCrispValueAvailable && isMembershipFunctionAvailable)	{
//				RuleEvaluationStep stub = new RuleEvaluationStep(membershipFunction, crispValue);
//				fuzzyStubs.put(waveformType, stub);
//			}
//		}
//		
//		return fuzzyStubs;
//	}
	
	private Map<OWLClass, RuleEvaluationStep> getFuzzyValuesForInferredRule(OWLClass inferredRule) {
		List<OWLClass> ruleWaveformTypes = helper.getRuleWaveformTypes(inferredRule);
		
		Map<OWLClass, OWLNamedIndividual> waveformOwlIndividualsForRule =
				getWaveformOwlIndividualsForRule(inferredRule);
		Map<OWLClass, Double> waveformCrispValues = 
				getWaveformCrispValues(waveformOwlIndividualsForRule);
		Map<OWLClass, Type2FuzzySet<MembershipFunction>> fuzzySetDefintionsForWaveforms = 
				getFuzzySetDefintionsForWaveforms(ruleWaveformTypes);
		Map<OWLClass, RuleEvaluationStep> fuzzySetValues = 
				getFuzzySetValues(ruleWaveformTypes, waveformCrispValues, fuzzySetDefintionsForWaveforms);

		return fuzzySetValues;
	}

	@Override
	public OWLNamedIndividual getOwlObject() {
		return owlIndividual;
	}

	public EcgLead getLead() {
		return lead;
	}
	
}
