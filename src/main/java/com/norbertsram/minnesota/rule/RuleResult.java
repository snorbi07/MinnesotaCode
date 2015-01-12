package com.norbertsram.minnesota.rule;

import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;

public class RuleResult {

	private final RuleEntity type;
	private final double degreeOfTruth;

	public RuleResult(RuleEntity type, double degreeOfTruth) {
		this.type = type;
		this.degreeOfTruth = degreeOfTruth;
	}

	public RuleEntity getType() {
		return type;
	}

	public double getDegreeOfTruth() {
		return degreeOfTruth;
	}
	
	@Override
	public String toString() {
		return type.toString() + ": " + degreeOfTruth;
	}

}
