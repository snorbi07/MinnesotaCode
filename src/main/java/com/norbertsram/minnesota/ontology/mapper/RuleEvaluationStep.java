package com.norbertsram.minnesota.ontology.mapper;

import java.util.Objects;

import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;

final class RuleEvaluationStep {

	private final Type2FuzzySet<MembershipFunction> type2FuzzySet;
	private final double crispValue;

	public RuleEvaluationStep(Type2FuzzySet<MembershipFunction> fuzzySet, double crispValue) {
		this.type2FuzzySet = Objects.requireNonNull(fuzzySet);
		this.crispValue = crispValue;
	}

	public double getCrispValue() {
		return crispValue;
	}

	public Type2FuzzySet<MembershipFunction> getType2FuzzySet() {
		return type2FuzzySet;
	}
	
}
