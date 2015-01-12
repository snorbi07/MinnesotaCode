package com.norbertsram.minnesota.ontology.mapper.entity;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

public enum LeadEntity implements OntologyPathProvider {
	
	I("Lead-I"),
	II("Lead-II"),
	III("Lead-III"),
	AVR("Lead-aVR"),
	AVL("Lead-aVL"),
	AVF("Lead-aVF"),
	V1("Lead-V1"),
	V2("Lead-V2"),
	V3("Lead-V3"),
	V4("Lead-V4"),
	V5("Lead-V5"),
	V6("Lead-V6");

	private final String path;
	
	LeadEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}
}
