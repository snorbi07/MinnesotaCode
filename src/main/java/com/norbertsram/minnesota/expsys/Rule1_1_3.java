package com.norbertsram.minnesota.expsys;

import java.util.List;

import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;

enum Rule1_1_3 implements RuleHandler {
	INSTANCE
	;

	@Override
	public boolean infer(RuleModel rule) {
		List<RuleProperty> properties = rule.getProperties();

		final int EXPECTED_NUMBER_OF_PROPERTIES = 2;
		if (properties.size() != EXPECTED_NUMBER_OF_PROPERTIES) {
			throw new IllegalStateException("Expected " + EXPECTED_NUMBER_OF_PROPERTIES + " properties, got: " +properties.size());
		}
	
		boolean result = true;
		for (RuleProperty property : properties) {
			switch (property.getProperty()) {
			case AMPLITUDE_R:
				double rAmplitude = property.getCrispValue();
				result &= rAmplitude >= 0.3; // 3mm = 0.3mv
				break;
				
			case INTERVAL_Q:
				double qInterval = property.getCrispValue();
				result &= qInterval >= 0.04;
				break;

			default:
				throw new IllegalStateException("Unsupported property type: " + property.getProperty());
			}
		}
		
		return result;
	}

}
