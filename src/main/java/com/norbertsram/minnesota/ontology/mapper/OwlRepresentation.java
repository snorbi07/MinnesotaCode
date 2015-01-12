package com.norbertsram.minnesota.ontology.mapper;

import org.semanticweb.owlapi.model.OWLObject;

interface OwlRepresentation<T extends OWLObject> {
	T getOwlObject();
}
