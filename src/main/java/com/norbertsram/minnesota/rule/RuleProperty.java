package com.norbertsram.minnesota.rule;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Objects;

import com.google.common.base.Objects.ToStringHelper;
import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.model.EcgData;
import com.norbertsram.flt.common.Interval;
import com.norbertsram.flt.mf.IndicatorMembershipFunction;
import com.norbertsram.flt.mf.MembershipFunction;
import com.norbertsram.flt.variable.type2.Type2FuzzySet;

// TODO: refactor, genaral rule property should not include fuzzy logic
public class RuleProperty {
	
	private final EcgProperty property;
    private final MembershipFunction type1FuzzySet;
	private final Type2FuzzySet<MembershipFunction> type2FuzzySet;
	private final double crispValue;
	private final EcgLead ecgLead;

	public RuleProperty(EcgProperty property, EcgLead ecgLead, MembershipFunction type1FuzzySet, Type2FuzzySet<MembershipFunction> type2FuzzySet, double crispValue) {
        this.property = Objects.requireNonNull(property);
        this.type1FuzzySet = Objects.requireNonNull(type1FuzzySet);
        this.type2FuzzySet = Objects.requireNonNull(type2FuzzySet);
        this.crispValue = crispValue;
        this.ecgLead = ecgLead;
    }

	public EcgProperty getProperty() {
		return property;
	}

	public Interval getFuzzyType2DegreeOfTruth() {
		IndicatorMembershipFunction valueFor = (IndicatorMembershipFunction) type2FuzzySet.valueFor(crispValue);

		Interval interval = valueFor.getInterval();

		return interval;
	}
    
    public double getFuzzyType1DegreeOfTruth() {
        return type1FuzzySet.valueFor(crispValue);
    }
	
	public double getCrispValue() {
		return crispValue;
	}

	public Type2FuzzySet<MembershipFunction> getType2FuzzySet() {
		return type2FuzzySet;
	}

	public EcgLead getEcgLead() {
		return ecgLead;
	}

	@Override
	public String toString() {
		ToStringHelper ts = toStringHelper(getClass());
		return ts.add("property", property).add("crispValue", crispValue)
				.add("type2FuzzySet", type2FuzzySet).toString();
	}


}
