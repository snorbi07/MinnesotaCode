package com.norbertsram.minnesota.ontology.mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;
import com.norbertsram.minnesota.MinnesotaException;
import com.norbertsram.minnesota.ontology.mapper.entity.GroupEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyDescription;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.PropertyEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.WaveformEntity;

class OntologyHelper {

	private static final Logger LOG = LoggerFactory
			.getLogger(OntologyHelper.class);

	private final OWLOntologyManager manager;
	private final OWLDataFactory factory;
	private final OWLOntology ontology;
	private final OWLReasoner reasoner;

	public OntologyHelper(InputStream ontologyFile) {
		manager = OWLManager.createOWLOntologyManager();
		factory = OWLManager.getOWLDataFactory();
		ontology = loadOntology(ontologyFile);
		reasoner = createReasoner(ontology);
	}

	public OntologyHelper() {
		this(null);
	}

	private OWLReasoner createReasoner(OWLOntology ontology) {
		OWLReasoner reasoner = null;
		if (ontology != null) {
			reasoner = new Reasoner(ontology);
		} 
		else {
			LOG.info("No ontology specified, skipping reasoner creation!");
		}

		return reasoner;
	}

	private OWLOntology loadOntology(InputStream ontologyFile) {
		OWLOntology ontology = null;

		if (ontologyFile != null) {
			try {
				ontology = 
						getManager().loadOntologyFromOntologyDocument(ontologyFile);
			} 
			catch (OWLOntologyCreationException ex) {
				throw new MinnesotaException("Unable to load ontology!", ex);
			}
		} 
		else {
			LOG.info("No ontology file to load!");
		}

		return ontology;
	}

	OWLClass getOwlClass(OntologyPathProvider path) {
		IRI iri = IRI.create(path.getPath());
		OWLClass owlClass = getFactory().getOWLClass(iri);
		return owlClass;
	}

	OWLNamedIndividual getOwlIndividual(OntologyPathProvider path) {
		IRI iri = IRI.create(path.getPath());
		OWLNamedIndividual owlNamedIndividual = getFactory().getOWLNamedIndividual(iri);
		return owlNamedIndividual;
	}
	
	OWLDataProperty getOwlDataProperty(OntologyPathProvider entity) {
		String path = entity.getPath();
		IRI iri = IRI.create(path);
		OWLDataProperty dataProperty = factory.getOWLDataProperty(iri);
		return dataProperty;
	}
	
	OWLObjectProperty getOwlObjectProperty(OntologyPathProvider property) {
		IRI iri = IRI.create(property.getPath());
		OWLObjectProperty owlObjectProperty = 
				factory.getOWLObjectProperty(iri);
		return owlObjectProperty;
	}

	OWLNamedIndividual createOwlIndividual(String name) {
		String path = OntologyDescription.NAMESPACE + name;
		IRI iri = IRI.create(path);
		LOG.debug("Creating ontology individual '{}'.", path);
		OWLNamedIndividual owlNamedIndividual = getFactory()
				.getOWLNamedIndividual(iri);
		return owlNamedIndividual;
	}


	OWLClassAssertionAxiom createOwlIndividualToClassAssertation(
			OWLIndividual individual, OWLClass klass) {
		OWLClassAssertionAxiom owlClassAssertionAxiom = getFactory()
				.getOWLClassAssertionAxiom(klass, individual);
		return owlClassAssertionAxiom;
	}

	void addAxiomToOntology(OWLAxiom axiom) {
		getManager().addAxiom(ontology, axiom);
	}

	OWLOntologyManager getManager() {
		return manager;
	}

	OWLDataFactory getFactory() {
		return factory;
	}

	OWLReasoner getReasoner() {
		return reasoner;
	}

	List<OWLClass> getOwlSubClassesForWaveformType(OntologyPathProvider type) {
		OWLClass owlClass = getOwlClass(type);
		boolean onlyDirectSubClasses = true;
		NodeSet<OWLClass> subClasses = 
				reasoner.getSubClasses(owlClass, onlyDirectSubClasses);
		Set<OWLClass> flattened = subClasses.getFlattened();

		return new ArrayList<>(flattened);
	}

	OWLAnnotation getFuzzyValueAnnotation(OWLClass owlClass) {
		String fuzzyValuePath = PropertyEntity.FUZZY_VALUE.getPath();
		IRI fuzzyValueIri = IRI.create(fuzzyValuePath);
		OWLAnnotationProperty fuzzyValueAnnotationProperty = 
				factory.getOWLAnnotationProperty(fuzzyValueIri);
		Set<OWLAnnotation> annotations = 
				owlClass.getAnnotations(ontology, fuzzyValueAnnotationProperty);

		final int NUMBER_OF_ALLOWED_FUZZY_ANNOTATIONS = 1;
		boolean ambiguousDefinitions = annotations.size() > NUMBER_OF_ALLOWED_FUZZY_ANNOTATIONS;
		if (ambiguousDefinitions) {
			LOG.error(
					"OWL class '{}' has '{}' fuzzy value annotations! Ignoring additional entries!",
					owlClass, annotations.size());
		}

		OWLAnnotation fuzzyValueAnnotation = null;
		if (!annotations.isEmpty()) {
			fuzzyValueAnnotation = Iterators.getOnlyElement(annotations.iterator());
		}

		return fuzzyValueAnnotation;
	}
	
	List<OWLNamedIndividual> getSampleOwlIndividuals() {
		OWLClass sampleClass = getOwlClass(GroupEntity.SAMPLE);
		boolean excludeSubclasses = true;
		NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(sampleClass, excludeSubclasses);
		Set<OWLNamedIndividual> flattened = instances.getFlattened();
		return new ArrayList<>(flattened);
	}
	
	List<OWLClass> getOwlIndividualTypes(OWLNamedIndividual sampleIndividual) {
		boolean excludeSubclasses = true;
		NodeSet<OWLClass> types = reasoner.getTypes(sampleIndividual, excludeSubclasses);
		Set<OWLClass> flattened = types.getFlattened();
		
		return new ArrayList<>(flattened);
	}
	
	List<OWLNamedIndividual> getOwlIndividualWaveformValues(OWLNamedIndividual individual) {
		OWLObjectProperty hasWaveformObjectProperty = getOwlObjectProperty(PropertyEntity.HAS_WAVEFORM);
		NodeSet<OWLNamedIndividual> objectPropertyValues = reasoner.getObjectPropertyValues(individual, hasWaveformObjectProperty);
		List<OWLNamedIndividual> values = new ArrayList<>(objectPropertyValues.getFlattened());
		
		return values;
	}
	
	OWLLiteral getOwlIndividualCrispValue(OWLNamedIndividual individual) {
		OWLDataProperty hasCrispValueDataProperty = getOwlDataProperty(PropertyEntity.HAS_CRISP_VALUE);
		Set<OWLLiteral> dataPropertyValues = reasoner.getDataPropertyValues(individual, hasCrispValueDataProperty);
		
		boolean hasCrispValue = true;
		int numCripsValues = dataPropertyValues.size(); 
		if (numCripsValues > 1) {
			LOG.error("OWL individual '{}' has crisp '{}' values! Ignoring additional entries!", individual, numCripsValues);
		}
		if (dataPropertyValues.isEmpty()) {
			LOG.error("OWL individual '{}' has no crisp value defined!", individual);
			hasCrispValue = false;
		}
		
		OWLLiteral crispValue = null;
		if (hasCrispValue) {
			crispValue = Iterators.getOnlyElement(dataPropertyValues.iterator());
		}
		
		return crispValue;
	}
	
	List<OWLNamedIndividual> getOwlIndividualsOfType(List<OWLNamedIndividual> individuals, OntologyPathProvider type) {
		OWLClass owlClass = getOwlClass(type);
		return getOwlIndividualsOfType(individuals, owlClass);
	}

	List<OWLNamedIndividual> getOwlIndividualsOfType(List<OWLNamedIndividual> individuals, OWLClass owlClass) {
		List<OWLNamedIndividual> result = new ArrayList<>();
		
		for (OWLNamedIndividual individual : individuals) {
			List<OWLClass> owlIndividualTypes = getOwlIndividualTypes(individual);
			boolean hasRequieredType = owlIndividualTypes.contains(owlClass);
			if (hasRequieredType) {
				result.add(individual);
			}
		}
		
		return result;
	}
	
	List<OWLClass> getInferredRuleTypes(OWLNamedIndividual sampleIndividual) {
		List<OWLClass> types = getOwlIndividualTypes(sampleIndividual);
		List<OWLClass> ruleTypes = new ArrayList<>();
		
		for (OWLClass type : types) {
			boolean isRule = isRuleType(type);
			if (isRule) {
				ruleTypes.add(type);
			}
		}
		
		if (ruleTypes.isEmpty()) {
			ruleTypes = Collections.emptyList();
		}
		
		return ruleTypes;
	}

	private OWLEquivalentClassesAxiom getRuleOwlEquivalentClassAxiom(OWLClass ruleType) {
		if (!isRuleType(ruleType)) {
			throw new IllegalArgumentException("Specified OWL class '" + ruleType + "' is not a rule definition!");
		}
		Set<OWLEquivalentClassesAxiom> equivalentClassesAxioms = ontology.getEquivalentClassesAxioms(ruleType);
		if (equivalentClassesAxioms.isEmpty()) {
			throw new IllegalStateException("Specified OWL class '" + ruleType + "' is not a valid rule definition!");
		}
		
		OWLEquivalentClassesAxiom ruleEquivalentDefinition = Iterators.getOnlyElement(equivalentClassesAxioms.iterator());
		return ruleEquivalentDefinition;
	}
	
	List<OWLClass> getRuleWaveformTypes(OWLClass ruleClass) {
		OWLEquivalentClassesAxiom ruleType = getRuleOwlEquivalentClassAxiom(ruleClass);
		List<OWLClass> result = Collections.emptyList();
		Set<OWLClassExpression> classExpressions = ruleType.getClassExpressions();
		for (OWLClassExpression expr : classExpressions) {
			ClassExpressionType classExpressionType = expr.getClassExpressionType();
			if (ClassExpressionType.OBJECT_INTERSECTION_OF.equals(classExpressionType)) {
				Set<OWLClass> waveformTypes = expr.getClassesInSignature();
				result = new ArrayList<>(waveformTypes);
			}
		}
		
		return result;
	}
	
	List<OWLNamedIndividual> getRuleLeads(OWLClass ruleClass) {
		OWLEquivalentClassesAxiom ruleType = getRuleOwlEquivalentClassAxiom(ruleClass);
		List<OWLNamedIndividual> result = Collections.emptyList();
		Set<OWLClassExpression> classExpressions = ruleType.getClassExpressions();
		for (OWLClassExpression expr : classExpressions) {
			ClassExpressionType classExpressionType = expr.getClassExpressionType();
			if (ClassExpressionType.OBJECT_INTERSECTION_OF.equals(classExpressionType)) {
				Set<OWLNamedIndividual> leads = expr.getIndividualsInSignature();
				result = new ArrayList<>(leads);
			}
		}
		
		return result;
	}
	
	OWLClass getWaveformFamilyForSubtype(OWLClass owlClass) {
		if (!isWaveformType(owlClass)) {
			throw new IllegalArgumentException("Given class '" + owlClass + "' is not a waveform type!");
		}
		
		final boolean directParentOnly = true;
		NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, directParentOnly);
		Set<OWLClass> parents = superClasses.getFlattened();
		if (parents.isEmpty()) {
			throw new IllegalArgumentException("Waveform type '" + owlClass + "' does not have a parent!");
		}
		int size = parents.size();
		boolean isValidWaveformDefinition = size == 1;
		if (!isValidWaveformDefinition) {
			throw new IllegalStateException("Waveform type '" + owlClass + "' has '" + size + "'  parents!");
		}
		
		OWLClass parent = Iterators.getOnlyElement(parents.iterator());
		
		return parent;
	}
	
	boolean isWaveformType(OWLClass owlClass) {
		final boolean doIncludeIndirectParents = false;
		NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, doIncludeIndirectParents);
		Set<OWLClass> allParents = superClasses.getFlattened();
		final String waveformOntologyPath = WaveformEntity.WAVEFORM.getPath();
		boolean isWaveform = false;
		for (OWLClass parent : allParents) {
			String stringID = parent.toStringID();
			isWaveform |= waveformOntologyPath.equals(stringID);
		}
		
		return isWaveform;
	}
	
	public void saveOntology(String fileName) {
		Objects.requireNonNull(fileName, "Must specify a valid file name!");
		File outputFile = new File(fileName);
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputFile);
			manager.saveOntology(ontology, outputStream);
			manager.saveOntology(ontology);
		} catch (FileNotFoundException | OWLOntologyStorageException e) {
			throw new MinnesotaException("Unable to save ontology to file: " + fileName, e);
		}
	}
	
	static boolean isRuleType(OWLClass owlClass) {
		boolean isRule = false;
		String classIri = owlClass.toStringID();
		for (RuleEntity rule : RuleEntity.values()) {
			String path = rule.getPath();
			if (path.equals(classIri)) {
				isRule = true;
				break;
			}
		}
		
		return isRule;
	}
	
}
