package com.norbertsram.ecgapi.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;

public final class EcgLeadValue {

	private final EcgLead lead;
	private final Map<EcgProperty, Double> values;
	
	public EcgLeadValue(EcgLead lead) {
		this.lead = lead;
		values = new EnumMap<>(EcgProperty.class);
	}
	
	public void setValue(EcgProperty property, double value) {
		if (property == null) {
			throw new IllegalArgumentException("Must specify a valid Ecg property!");
		}
		values.put(property, value);
	}
	
	public Double getValue(EcgProperty property) {
		if (property == null) {
			throw new IllegalArgumentException("Must specify a valid Ecg property!");
		}
		return values.get(property);
	}
	
	public EcgLead getLead() {
		return lead;
	}
	
	public List<EcgProperty> getEcgProperties() {
		return new ArrayList<>(values.keySet());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	
		sb.append("lead=").append(lead).append("\n");
		for (Entry<EcgProperty, Double> entry : values.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			sb.append(key).append("=").append(value).append("\n");
		}
		
		return sb.toString();
	}
	
}