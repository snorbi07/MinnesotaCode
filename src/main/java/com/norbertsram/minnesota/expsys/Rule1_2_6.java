package com.norbertsram.minnesota.expsys;

import java.util.List;

import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;

enum Rule1_2_6 implements RuleHandler {
	INSTANCE
	;

	@Override
	public boolean infer(RuleModel rule) {
		List<RuleProperty> properties = rule.getProperties();

		final int EXPECTED_NUMBER_OF_PROPERTIES = 1;
		if (properties.size() != EXPECTED_NUMBER_OF_PROPERTIES) {
			throw new IllegalStateException("Expected " + EXPECTED_NUMBER_OF_PROPERTIES + " properties, got: " +properties.size());
		}
	
		boolean result = true;
		for (RuleProperty property : properties) {
			switch (property.getProperty()) {
			case AMPLITUDE_Q:
				double qAmplitude = property.getCrispValue();
				result &= qAmplitude >= 0.5; // 5mm = 0.5mv
				break;

			default:
				throw new IllegalStateException("Unsupported property type: " + property.getProperty());
			}
		}
		
		return result;
	}

}
