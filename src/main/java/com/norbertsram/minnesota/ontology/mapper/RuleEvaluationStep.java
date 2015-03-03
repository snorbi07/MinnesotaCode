package com.norbertsram.minnesota.ontology.mapper;

import java.util.Objects;

import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;

final class RuleEvaluationStep {

    private final MembershipFunction type1FuzzySet;
	private final Type2FuzzySet<MembershipFunction> type2FuzzySet;
	private final double crispValue;

	public RuleEvaluationStep(MembershipFunction type1FuzzySet, Type2FuzzySet<MembershipFunction> type2FuzzySet, double crispValue) {
        this.type1FuzzySet = Objects.requireNonNull(type1FuzzySet);
        this.type2FuzzySet = Objects.requireNonNull(type2FuzzySet);
        this.crispValue = crispValue;
    }

	public double getCrispValue() {
		return crispValue;
	}

    public MembershipFunction getType1FuzzySet() { return type1FuzzySet; }
    
	public Type2FuzzySet<MembershipFunction> getType2FuzzySet() {
		return type2FuzzySet;
	}
	
}
