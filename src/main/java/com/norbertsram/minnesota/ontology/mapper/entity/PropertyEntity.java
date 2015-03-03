package com.norbertsram.minnesota.ontology.mapper.entity;

public enum PropertyEntity implements OntologyPathProvider {
	
	HAS_WAVEFORM("hasWaveform"),
	HAS_CRISP_VALUE("hasCrispValue"),
	HAS_LEAD("hasLead"),
	TYPE_2_FUZZY_VALUE("fuzzyValue"),
	TYPE_1_FUZZY_VALUE("type1FuzzyValue");

	private final String path;
	
	PropertyEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}
}
