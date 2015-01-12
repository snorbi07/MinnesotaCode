package com.norbertsram.minnesota.ontology.mapper;

import java.io.InputStream;

import org.semanticweb.owlapi.model.OWLClass;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

public class OntologyBuilder {
	
	private final OntologyHelper helper;
	
	public OntologyBuilder(InputStream ontologyFile) {
		helper = new OntologyHelper(ontologyFile);
	}

	public Sample createSample(String name, EcgLead lead) {
		return new Sample(helper, name, lead);
	}
	
	public HasWaveformAxiom createHasWaveformAxiom(Sample sample,
			Waveform waveform) {
		return new HasWaveformAxiom(helper, sample, waveform);
	}
	
	public Waveform createWaveform(OntologyPathProvider type, String name) {
		return new Waveform(helper, type, name);
	}
	
	public Waveform createWaveform(OWLClass type, String name) {
		return new Waveform(helper, type, name);
	}
}
