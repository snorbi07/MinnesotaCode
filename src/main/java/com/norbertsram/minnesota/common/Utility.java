package com.norbertsram.minnesota.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utility {

	private static final Logger LOG = 
			LoggerFactory.getLogger(Utility.class);
	
	public static double parseValue(String rawValue) {
		final double FALLBACK_VALUE = Double.NaN;

		LOG.debug("Parsing value {}.", rawValue);
		double result = FALLBACK_VALUE;
		try {
			result = Double.parseDouble(rawValue);
		} catch (NumberFormatException ex) {
			LOG.debug("Invalid value format '{}', reverting to fallback!", rawValue);
			assert result == FALLBACK_VALUE : "In case of error result must be equal to fallback value!";
		}

		return result;
	}

}