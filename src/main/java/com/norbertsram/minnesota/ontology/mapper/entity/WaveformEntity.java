package com.norbertsram.minnesota.ontology.mapper.entity;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

public enum WaveformEntity implements OntologyPathProvider {
	
	WAVEFORM("Waveform"),

	// ----- AMPLITUDE MAPPINGS ----- //
	AMPLITUDE_P_WAVE("PWaveAmplitude"),
	AMPLITUDE_P_WAVE_HIGH("PWaveAmplitudeHigh"),
	AMPLITUDE_P_WAVE_LOW("PWaveAmplitudeLow"),
	AMPLITUDE_P_WAVE_NORMAL("PWaveAmplitudeNormal"),

	AMPLITUDE_QRS("QRSAmplitude"),
	AMPLITUDE_QRS_HIGH("QRSAmplitudeHigh"),
	AMPLITUDE_QRS_LOW("QRSAmplitudeLow"),
	AMPLITUDE_QRS_NORMAL("QRSAmplitudeNormal"),

	AMPLITUDE_R("RAmplitude"),
	AMPLITUDE_R_LOW("RAmplitudeLow"),
	AMPLITUDE_R_HIGH("RAmplitudeHigh"),

	AMPLITUDE_Q("QAmplitude"),
	AMPLITUDE_Q_LOW("QAmplitudeLow"),
	AMPLITUDE_Q_NORMAL("QAmplitudeNormal"),

	AMPLITUDE_S("SAmplitude"),
	AMPLITUDE_S_LOW("SAmplitudeLow"),
	AMPLITUDE_S_HIGH("SAmplitudeHigh"),

	// ----- RATIO MAPPINGS ----- //
	RATIO_QR("Q/RAmplitudeRatio"),
	RATIO_QR_LOWER_TRESHOLD("Q/RAmplitudeRatioLowerTreshold"),
	RATIO_QR_UPPER_TRESHOLD("Q/RAmplitudeRatioUpperTreshold"),
	RATIO_QR_TRANSITION("Q/RAmplitudeRatioTransition"),
	
	RATIO_TR("T/RAmplitudeRatio"),	
	RATIO_TR_LOW("T/RAmplitudeRatioLow"),	
	RATIO_TR_NORMAL("T/RAmplitudeRatioNormal"),

	// ----- INTERVAL MAPPINGS ----- //
	INTERVAL_PR("PRInterval"),
	INTERVAL_PR_LONG("PRIntervalLong"),
	INTERVAL_PR_SHORT("PRIntervalShort"),
	
	INTERVAL_Q("QDuration"),
	INTERVAL_Q_LONG("QDurationLong"),
	INTERVAL_Q_LONG_RANGE("QDurationLongRange"),
	INTERVAL_Q_MEDIUM("QDurationMedium"),
	INTERVAL_Q_MEDIUM_RANGE("QDurationMediumRange"),
	INTERVAL_Q_SHORT("QDurationShort"),
	INTERVAL_Q_SHORT_RANGE("QDurationShortRange"),
	
	INTERVAL_QRS("QRSDuration"),
	INTERVAL_QRS_LONG("QRSDurationLong"),
	INTERVAL_QRS_SHORT("QRSDurationShort"),

	// ----- VALUE MAPPINGS ----- //
	VALUE_AV_BLOCK("AVBlock"),
	VALUE_AV_BLOCK_INTERMITTENT("AVBlockIntermittent"),
	VALUE_AV_BLOCK_PARTIAL("AVBlockPartial"),
	VALUE_AV_BLOCK_PERMANENT("AVBlockPermanent"),
	
	VALUE_QRS("QRSWave"),
	VALUE_QRS_NEGATIVE("QRSWaveNegative"),
	VALUE_QRS_POSITIVE("QRSWavePositive"),
	
	VALUE_QS("QSPattern"),
	
	VALUE_R_AMPLITUDE_MONOTONITY("RAmplitudeMonotonity"),
	VALUE_R_AMPLITUDE_MONOTONITY_DECREASING("RAmplitudeMonotonityDecreasing"),
	VALUE_R_AMPLITUDE_MONOTONITY_INCREASING("RAmplitudeMonotonityIncreasing"),
	VALUE_R_AMPLITUDE_MONOTONITY_MONOTON("RAmplitudeMonotonityMonoton"),
	
	
	VALUE_STJ_DEPRESSION("STJDepression"),
	VALUE_STJ_DEPRESSION_HIGH("STJDepressionHigh"),
	VALUE_STJ_DEPRESSION_LOW("STJDepressionLow"),
	VALUE_STJ_DEPRESSION_LOWER_RANGE("STJDepressionLowerRange"),
	VALUE_STJ_DEPRESSION_UPPER_RANGE("STJDepressionUpperRange"),
	
	VALUE_ST_SEGMENT("STSegment"),
	VALUE_ST_SEGMENT_DOWNWARD_SLOPING("STSegmentDownwardSloping"),
	VALUE_ST_SEGMENT_HORIZONTAL("STSegmentHorizontal"),
	VALUE_ST_SEGMENT_USHAPED("STSegmentUShaped"),
	
	VALUE_ST_SEGMENT_ELEVATION("STSegmentElevation"),
	VALUE_ST_SEGMENT_ELEVATION_ABOVE_NORMAL("STSegmentElevationAboveNormal"),
	VALUE_ST_SEGMENT_ELEVATION_HIGH("STSegmentElevationHigh"),
	VALUE_ST_SEGMENT_ELEVATION_NORMAL("STSegmentElevationNormal"),

	VALUE_T_WAVE("TWave"),
	VALUE_T_WAVE_AMPLITUDE_VALUE("TWaveAmplitudeValue"),
	VALUE_T_WAVE_PHASE("TWavePhase"),

	// ----- AXIS MAPPINGS ----- //
	AXIS_QRS("QRSAxis"),
	AXIS_QRS_EXTREME("QRSAxisExtreme"),
	AXIS_QRS_INTERMEDIATE("QRSAxisIntermediate"),
	AXIS_QRS_LEFT("QRSAxisLeft"),
	AXIS_QRS_RIGHT("QRSAxisLeftRight"),
	;
	
	private final String path;
	
	WaveformEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}
	
	public static WaveformEntity getWaveformEntityForPath(String iriPath) {
		WaveformEntity result = null;
		for (WaveformEntity entity : WaveformEntity.values()) {
			String entityPath = entity.getPath();
			if (entityPath.equals(iriPath)) {
				result = entity;
			}
		}
		
		if (result == null) {
			throw new IllegalArgumentException("Missing waveform entity type for: " + iriPath);
		}
		
		return result;
	}
}
