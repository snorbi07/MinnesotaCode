package com.norbertsram.minnesota.expsys;

import java.util.List;

import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;

enum Rule1_3_1 implements RuleHandler {
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
			case RATIO_QR:
				double qrRatio = property.getCrispValue();
				result &= (qrRatio >= 0.2 && qrRatio < 0.33);
				break;
				
			case INTERVAL_Q:
				double qInterval = property.getCrispValue();
				result &= (qInterval >= 0.02 && qInterval < 0.03);
				break;

			default:
				throw new IllegalStateException("Unsupported property type: " + property.getProperty());
			}
		}
		
		return result;
	}

}
