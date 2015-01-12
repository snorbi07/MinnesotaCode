package com.norbertsram.minnesota.ontology.mapper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;
import com.norbertsram.minnesota.ontology.mapper.entity.WaveformEntity;

public class EcgPropertyMapperTest {

	@Test
	public void testSupportedMappings() {
		OntologyPathProvider ontologyMapping = 
				EcgPropertyMapper.getOntologyMapping(EcgProperty.INTERVAL_Q);
    	assertTrue(ontologyMapping == WaveformEntity.INTERVAL_Q);

    	ontologyMapping = EcgPropertyMapper.getOntologyMapping(EcgProperty.RATIO_QR);
    	assertTrue(ontologyMapping == WaveformEntity.RATIO_QR);
	}

	@Test
	public void testUnsupportedMappings() {
		OntologyPathProvider ontologyMapping = 
				EcgPropertyMapper.getOntologyMapping(EcgProperty.AMPLITUDE_J100);
		assertTrue(ontologyMapping == EcgPropertyMapper.UNSUPPORTED_MAPPING);
	}
	
	@Test
	public void testLeadMappings() {
		for (EcgLead lead : EcgLead.values()) {
			OntologyPathProvider ontologyMapping = EcgPropertyMapper.getOntologyMapping(lead);
			assertTrue(ontologyMapping != EcgPropertyMapper.UNSUPPORTED_MAPPING);
		}
	}
	
	
}
