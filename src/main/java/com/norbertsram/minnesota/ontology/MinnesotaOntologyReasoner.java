package com.norbertsram.minnesota.ontology;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.model.EcgData;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.ecgapi.model.EcgPatientData;
import com.norbertsram.flt.common.Interval;
import com.norbertsram.flt.operator.Min;
import com.norbertsram.minnesota.ontology.mapper.OntologyBuilder;
import com.norbertsram.minnesota.ontology.mapper.Sample;
import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;
import com.norbertsram.minnesota.rule.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class MinnesotaOntologyReasoner {

	private static final Logger LOG = LoggerFactory.getLogger(MinnesotaOntologyReasoner.class);
	
	private static final String DEFAULT_MINNESOTA_CODE_ONTOLOGY = "./ontology/MinnesotaCode.owl";

	public static Multimap<String, RuleModel> processEcgData(EcgData data) {
		Collection<EcgPatientData> patients = data.getData();

		Multimap<String, RuleModel> results = ArrayListMultimap.create();

		for (EcgPatientData patient : patients) {
			Collection<RuleModel> patientRuleResults = processPatientData(patient);
			results.putAll(patient.getPatientId(), patientRuleResults);
		}

		return results;
	}
	
	public static InputStream loadOntology() {
		String ontologyPath = DEFAULT_MINNESOTA_CODE_ONTOLOGY; // NOTE: dynamic ontology usage could be a useful feature
		InputStream resource = null;
		try {
			resource = new FileInputStream(ontologyPath);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Cannot find ontology file: " + ontologyPath);
		}
		// FIXME: file based loading is a temporary solution. Fix classpath based loading for each use case (unit test as well)
		//InputStream resource = MinnesotaOntologyReasoner.class.getResourceAsStream(DEFAULT_MINNESOTA_CODE_ONTOLOGY);
		InputStream ontologyFile = Objects.requireNonNull(resource);
		return ontologyFile;
	}

	public static Collection<RuleModel> processPatientData(EcgPatientData patient) {
		LOG.info("Processing patient data: {}.", patient.getPatientId());
		InputStream ontologyFile = loadOntology();
		OntologyBuilder builder = new OntologyBuilder(ontologyFile);

		List<RuleModel> result = new ArrayList<>();
		List<EcgLeadValue> leadValues = patient.getData();
		for (EcgLeadValue leadValue : leadValues) {
			Sample sample = toSample(leadValue, builder);
			List<RuleModel> ruleResults = sample.evaluate();
			result.addAll(ruleResults);
		}

		return result;
	}
	
	private static void validateRuleModel(RuleModel rule) {
		Objects.requireNonNull(rule);

		assert rule.getProperties() != null;
		if (rule.getProperties().isEmpty()) {
			throw new IllegalArgumentException("Corrupt or invalid rule model: " + rule.toString());
		}
	}

	/*
	First reduce the type-2 fuzzy intervals to a type-1 then aggregate using type-1 aggregation
	 */
	public static RuleResult typeReducedAggregation(RuleModel rule) {
		validateRuleModel(rule);
		Iterator<RuleProperty> properties = rule.getProperties().iterator();
		assert properties.hasNext();

		RuleProperty property = properties.next();
		Interval interval = property.getFuzzyType2DegreeOfTruth();

		double result = intervalToDegreeOfTruth(interval);

		while (properties.hasNext()) {
			property = properties.next();
			interval = property.getFuzzyType2DegreeOfTruth();
			double degreeOfTruth = intervalToDegreeOfTruth(interval);
			result = Min.INSTANCE.apply(result, degreeOfTruth);
		}

		return new RuleResult(rule.getType(), result);
	}
		
	private static double intervalToDegreeOfTruth(Interval interval) {
		return (interval.getUpperBound() + interval.getLowerBound()) / 2.0;
	}

	/*
	Calculate the distance between the intervals then execute the type-2 fuzzy reduction
	 */
	public static RuleResult distanceBasedReduction(RuleModel rule) {
		validateRuleModel(rule);
		Iterator<RuleProperty> properties = rule.getProperties().iterator();
		assert properties.hasNext();
		
		RuleProperty property = properties.next();
		Interval result = property.getFuzzyType2DegreeOfTruth();

		while(properties.hasNext()) {
			property = properties.next();
			Interval degreeOfTruth = property.getFuzzyType2DegreeOfTruth();
			if (degreeOfTruth.isIntersect(result)) {
				result = degreeOfTruth.intersect(result);
			}
			else {
				result = calculateDifference(result, degreeOfTruth);
			}
		}
		
		double degreeOfTruth = (result.getUpperBound() + result.getLowerBound()) / 2.0;
		
		return new RuleResult(rule.getType(), degreeOfTruth);
	}

	public static RuleResult type1FuzzyBasedAggregation(RuleModel rule) {
		validateRuleModel(rule);
		Iterator<RuleProperty> properties = rule.getProperties().iterator();
		assert properties.hasNext();
		
		RuleProperty property = properties.next();
        double result = property.getFuzzyType1DegreeOfTruth();

        while(properties.hasNext()) {
			property = properties.next();
			double degreeOfTruth = property.getFuzzyType1DegreeOfTruth();
            result = Min.INSTANCE.apply(result, degreeOfTruth);
        }
		
		return new RuleResult(rule.getType(), result);
	}
	
	private static Interval calculateDifference(Interval lhs, Interval rhs) {
		if (lhs.isIntersect(rhs)) {
			throw new IllegalArgumentException("Difference can only be calculated between disjoint sets!");
		}
		
		double lBound = 0.0;
		double uBound = 0.0;
		if (lhs.getUpperBound() < rhs.getUpperBound()) {
			lBound = lhs.getUpperBound();
			uBound = rhs.getLowerBound();
		}
		else {
			lBound = rhs.getUpperBound();
			uBound = lhs.getLowerBound();
		}
		
		return new Interval(lBound, uBound);
	}
	
	private static Sample toSample(EcgLeadValue leadValue, OntologyBuilder builder) {
		EcgLead lead = leadValue.getLead();
		String name = Integer.toString(leadValue.hashCode());

		Sample sample = builder.createSample(name, lead);
		sample.addValues(leadValue);

		return sample;
	}

}
