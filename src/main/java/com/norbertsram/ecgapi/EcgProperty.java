package com.norbertsram.ecgapi;

public enum EcgProperty {
	INTERVAL_P("intervalP", 0),
	INTERVAL_PQ("intervalPQ", 1),
	INTERVAL_QR("intervalQR", 2),
	INTERVAL_QRS("intervalQRS", 3),
	INTERVAL_Q("intervalQ", 4),
	INTERVAL_R("intervalR", 5),
	INTERVAL_S("intervalS", 6),
	INTERVAL_QT("intervalQT", 7),
	AMPLITUDE_P("amplitudeP", 8),
	AMPLITUDE_Q("amplitudeQ", 9),
	AMPLITUDE_R("amplitudeR", 10),
	AMPLITUDE_R1("amplitudeR1", 11),
	AMPLITUDE_S("amplitudeS", 12),
	AMPLITUDE_J("amplitudeJ", 13),
	AMPLITUDE_J80("amplitudeJ80", 14),
	AMPLITUDE_J100("amplitudeJ100", 15),
	AMPLITUDE_T("amplitudeT", 16),
	AREA_P("areaP", 17),
	AREA_R("areaR", 18),
	AREA_T("areaT", 19),
	RATIO_QR("ratioQR", 20),
	RATIO_RS("ratioRS", 21),
	RATIO_RR1("ratioRR1", 22),
	RATIO_TR("ratioTR", 23),
	VALUE_QTC("valueQTC", 24),
	DEVIATION_ST("deviationST", 25),
	VALUE_QRS_PEAK_TO_PEAK("valuePeakToPeakQRS", 26),
	VALUE_QRS_BIAS("valueQRSBias", 27)
	;
	
	EcgProperty(String propertyKey, int index) {
		this.propertyKey = propertyKey;
		this.index = index;
	}
	
	public String getPropertyKey() {
		return propertyKey;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return propertyKey;
	}

	private final String propertyKey;
	private final int index;
}
