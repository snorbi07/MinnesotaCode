package com.norbertsram.minnesota.ontology.mapper.entity;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

public enum GroupEntity implements OntologyPathProvider {
	
	RULE("Rule"),
	LEAD("Lead"),
	SAMPLE("Sample");
	
	private final String path;
	
	GroupEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}
}
