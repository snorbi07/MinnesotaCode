package com.norbertsram.minnesota.ontology.mapper;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.minnesota.ontology.mapper.entity.LeadEntity;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.WaveformEntity;

class EcgPropertyMapper {

	private static final Logger LOG = 
			LoggerFactory.getLogger(EcgPropertyMapper.class);
	
	static final OntologyPathProvider UNSUPPORTED_MAPPING = null;
	private static Map<EcgProperty, OntologyPathProvider> propertyMappings;
	private static Map<EcgLead, OntologyPathProvider> leadMappings;
	static {
		leadMappings = new EnumMap<>(EcgLead.class);
		leadMappings.put(EcgLead.AVF, LeadEntity.AVF);
		leadMappings.put(EcgLead.AVR, LeadEntity.AVR);
		leadMappings.put(EcgLead.AVL, LeadEntity.AVL);
		leadMappings.put(EcgLead.I, LeadEntity.I);
		leadMappings.put(EcgLead.II, LeadEntity.II);
		leadMappings.put(EcgLead.III, LeadEntity.III);
		leadMappings.put(EcgLead.V1, LeadEntity.V1);
		leadMappings.put(EcgLead.V2, LeadEntity.V2);
		leadMappings.put(EcgLead.V3, LeadEntity.V3);
		leadMappings.put(EcgLead.V4, LeadEntity.V4);
		leadMappings.put(EcgLead.V5, LeadEntity.V5);
		leadMappings.put(EcgLead.V6, LeadEntity.V6);
		
		propertyMappings = new EnumMap<>(EcgProperty.class);
		propertyMappings.put(EcgProperty.INTERVAL_Q, WaveformEntity.INTERVAL_Q);
		propertyMappings.put(EcgProperty.RATIO_QR, WaveformEntity.RATIO_QR);
		propertyMappings.put(EcgProperty.AMPLITUDE_P, WaveformEntity.AMPLITUDE_P_WAVE);
		propertyMappings.put(EcgProperty.AMPLITUDE_R, WaveformEntity.AMPLITUDE_R);
		propertyMappings.put(EcgProperty.AMPLITUDE_S, WaveformEntity.AMPLITUDE_S);
		propertyMappings.put(EcgProperty.INTERVAL_P, WaveformEntity.INTERVAL_PR);
		propertyMappings.put(EcgProperty.INTERVAL_QRS, WaveformEntity.INTERVAL_QRS);
	}
	
	static OntologyPathProvider getOntologyMapping(EcgProperty property) {
		OntologyPathProvider path = UNSUPPORTED_MAPPING;
		if (propertyMappings.containsKey(property)) {
			path = propertyMappings.get(property);
		}
		else {
			LOG.debug("Unsupported mapping '{}'!", property.getPropertyKey());
		}
		
		return path;
	}

	static OntologyPathProvider getOntologyMapping(EcgLead lead) {
		OntologyPathProvider path = UNSUPPORTED_MAPPING;
		if (leadMappings.containsKey(lead)) {
			path = leadMappings.get(lead);
		}
		else {
			LOG.debug("No mapping for lead:'{}'!", lead);
		}
		
		return path;
	}
	
	static EcgProperty getEcgProperty(WaveformEntity entity) {
		EcgProperty result = null;
		
		Set<Entry<EcgProperty,OntologyPathProvider>> entrySet = propertyMappings.entrySet();
		String waveformPath = entity.getPath();
		for (Entry<EcgProperty, OntologyPathProvider> entry : entrySet) {
			
			EcgProperty ecgProperty = entry.getKey();
			OntologyPathProvider mapping = entry.getValue();
			
			String mappingPath = mapping.getPath();
			if (mappingPath.equals(waveformPath)) {
				result = ecgProperty;
				break;
			}
		}
		
		if (result == null) {
			throw new IllegalArgumentException("Missing property for waveform: " + entity);
		}
		
		return result;
	}
	
}
