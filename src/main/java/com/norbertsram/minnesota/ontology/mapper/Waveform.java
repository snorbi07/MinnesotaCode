package com.norbertsram.minnesota.ontology.mapper;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.PropertyEntity;

public class Waveform implements OwlRepresentation<OWLIndividual> {
	
	private final OntologyHelper helper;
	private final OWLIndividual waveform;
	private final OWLClass waveformClass;
	private final OWLClassAssertionAxiom typeAssertation;
	
	private OWLDataPropertyAssertionAxiom hasCrispValueAssertionAxiom;
	private Double crispValue = null;
	
	Waveform(OntologyHelper helper, OntologyPathProvider type, String identifier) {
		this(helper, helper.getOwlClass(type), identifier);
	}

	Waveform(OntologyHelper helper, OWLClass waveformOwlClass, String identifier) {
		this.helper = helper;
		waveform = helper.createOwlIndividual(identifier);
		waveformClass = waveformOwlClass;
		typeAssertation = 
				helper.getFactory().getOWLClassAssertionAxiom(waveformClass, waveform);
		helper.addAxiomToOntology(typeAssertation);
	}
	
	@Override
	public OWLIndividual getOwlObject() {
		return waveform;
	}

	public Double getCrispValue() {
		return crispValue;
	}

	public void setCrispValue(Double crispValue) {
		this.crispValue = crispValue;
		updateOntologyRepresentation();
	}
	
	public OWLClass getWaveformClass() {
		return waveformClass;
	}
	
	private void updateOntologyRepresentation() {
		if (crispValue == null) {
			throw new IllegalStateException("Waveform crisp value is not specified!");
		}
		
		OWLDataFactory factory = helper.getFactory();
		String path = PropertyEntity.HAS_CRISP_VALUE.getPath();
		IRI iri = IRI.create(path);
		OWLDataProperty hasCrispValue = factory.getOWLDataProperty(iri);
		hasCrispValueAssertionAxiom = 
				factory.getOWLDataPropertyAssertionAxiom(hasCrispValue, waveform, crispValue);
		helper.addAxiomToOntology(hasCrispValueAssertionAxiom);
	}
	
}
