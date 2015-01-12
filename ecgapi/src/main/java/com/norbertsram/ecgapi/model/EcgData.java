package com.norbertsram.ecgapi.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class EcgData {

	private Collection<EcgPatientData> data;
	
	public EcgData() {
		data = new ArrayList<>();
	}
	
	public EcgData add(EcgPatientData value) {
		data.add(value);
		return this;
	}
	
	public EcgData addAll(List<EcgPatientData> values) {
		data.addAll(values);
		return this;
	}
	
	public Collection<EcgPatientData> getData() {
		return Collections.unmodifiableCollection(data);
	}
	
}
