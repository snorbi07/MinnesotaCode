package com.norbertsram.minnesota.ontology.mapper;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import com.norbertsram.minnesota.ontology.mapper.entity.PropertyEntity;

public class HasWaveformAxiom implements OwlRepresentation<OWLObjectPropertyAssertionAxiom> {

	private final OntologyHelper helper;
	private final OWLObjectProperty hasWaveform;
	private final OWLObjectPropertyAssertionAxiom propertyAssertationAxiom;
	private final Waveform waveform;
	
	HasWaveformAxiom(OntologyHelper ontologyBuilder, Sample sample, Waveform waveform) {
		this.helper = Objects.requireNonNull(ontologyBuilder);
		this.waveform = waveform;
		this.hasWaveform = helper.getOwlObjectProperty(PropertyEntity.HAS_WAVEFORM);
		
		OWLIndividual sampleOwlIndividual = sample.getOwlObject();
		OWLIndividual waveformOwlIndividual = waveform.getOwlObject();
		
		if (sampleOwlIndividual == null || waveformOwlIndividual == null) {
			throw new IllegalArgumentException("Specified patient and/or waveform has no ontology representation available!");
		}
		OWLDataFactory factory = helper.getFactory();
		propertyAssertationAxiom = 
				factory.getOWLObjectPropertyAssertionAxiom(hasWaveform, sampleOwlIndividual, waveformOwlIndividual);
		helper.addAxiomToOntology(propertyAssertationAxiom);
	}

	@Override
	public OWLObjectPropertyAssertionAxiom getOwlObject() {
		return propertyAssertationAxiom;
	}
	
	public Waveform getWaveform() {
		return waveform;
	}
	
}
