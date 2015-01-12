package com.norbertsram.minnesota.ontology.mapper.entity;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

public enum PropertyEntity implements OntologyPathProvider {
	
	HAS_WAVEFORM("hasWaveform"),
	HAS_CRISP_VALUE("hasCrispValue"),
	HAS_LEAD("hasLead"),
	FUZZY_VALUE("fuzzyValue");

	private final String path;
	
	PropertyEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}
}
