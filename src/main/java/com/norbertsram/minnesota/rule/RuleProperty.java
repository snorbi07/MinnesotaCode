package com.norbertsram.minnesota.rule;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Objects;

import com.google.common.base.Objects.ToStringHelper;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.flt.common.Interval;
import com.norbertsram.flt.mf.IndicatorMembershipFunction;
import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;

// TODO: refactor, genaral rule property should not include fuzzy logic
public class RuleProperty {
	
	private final EcgProperty property;
	private final Type2FuzzySet<MembershipFunction> type2FuzzySet;
	private final double crispValue;

	public RuleProperty(EcgProperty property, Type2FuzzySet<MembershipFunction> type2FuzzySet, double crispValue) {
		this.property = Objects.requireNonNull(property);
		this.type2FuzzySet = Objects.requireNonNull(type2FuzzySet);
		this.crispValue = crispValue;
	}

	public EcgProperty getProperty() {
		return property;
	}

	public Interval getDegreeOfTruth() {
		IndicatorMembershipFunction valueFor = (IndicatorMembershipFunction) type2FuzzySet.valueFor(crispValue);

		Interval interval = valueFor.getInterval();

		return interval;
	}
	
	public double getCrispValue() {
		return crispValue;
	}

	public Type2FuzzySet<MembershipFunction> getType2FuzzySet() {
		return type2FuzzySet;
	}

	@Override
	public String toString() {
		ToStringHelper ts = toStringHelper(getClass());
		return ts.add("property", property).add("crispValue", crispValue)
				.add("type2FuzzySet", type2FuzzySet).toString();
	}


}
