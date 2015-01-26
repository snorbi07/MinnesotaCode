package com.norbertsram.ecgapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.norbertsram.ecgapi.EcgLead;

public class EcgPatientData {

	private final String patientId;
	private final String description;
	private final List<com.norbertsram.ecgapi.model.EcgLeadValue> data;

	public EcgPatientData(String patientId, String description) {
		this.patientId = Objects.requireNonNull(patientId);
		this.description = description;
		this.data = new ArrayList<>();
	}
	
	public EcgPatientData add(com.norbertsram.ecgapi.model.EcgLeadValue value) {
		data.add(value);
		return this;
	}

	public List<com.norbertsram.ecgapi.model.EcgLeadValue> getData() {
		return Collections.unmodifiableList(data);
	}

	public String getDescription() {
		return description;
	}
	
	public List<EcgLead> getAvailableLeads() {
		Set<EcgLead> leads = new HashSet<>();
		for (com.norbertsram.ecgapi.model.EcgLeadValue leadValue : data) {
			leads.add(leadValue.getLead());
		}
		
		return new ArrayList<>(leads);
	}
	
	public com.norbertsram.ecgapi.model.EcgLeadValue getEcgLeadValue(EcgLead lead) {
		Objects.requireNonNull(lead);
		
		com.norbertsram.ecgapi.model.EcgLeadValue result = null;
		for (com.norbertsram.ecgapi.model.EcgLeadValue leadValue : data) {
			if (lead == leadValue.getLead()) {
				result = leadValue;
			}
		}
		
		if (result == null) {
			throw new IllegalArgumentException("No lead values found for lead: " + lead);
		}
		return result;
	}
	
	public String getPatientId() {
		return patientId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("description={ ").append(description).append(" }\n");
		List<EcgLead> leads = getAvailableLeads();
		sb.append("leads={ ");
		for (EcgLead lead : leads) {
			sb.append(lead).append(" ");
		}
		sb.append("}\n");
		
		return sb.toString();
	}
	
}
