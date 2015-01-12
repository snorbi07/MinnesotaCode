package com.norbertsram.minnesota.io.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.norbertsram.ecgapi.EcgLead;
import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.EcgReader;
import com.norbertsram.ecgapi.model.EcgData;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.ecgapi.model.EcgPatientData;
import com.norbertsram.minnesota.common.Utility;

public class CsvParser implements EcgReader {

	private static final Logger LOG = 
			LoggerFactory.getLogger(CsvParser.class);
	private static final int GENERAL_DATA_SIZE = 11;
	private static final int ECG_LEAD_DATA_SIZE = 28;

	private final InputStream inputDatasetStream;

	public CsvParser(InputStream dataset) {
		inputDatasetStream = Objects.requireNonNull(dataset);
	}

	@Override
	public EcgData read() {
		LOG.info("Parsing file {}.", inputDatasetStream);
		if (inputDatasetStream == null) {
			throw new IllegalStateException("Must specify a valid input path!");
		}
		EcgData result = null;
		try {
			CSVReader csvReader = new CSVReader(new InputStreamReader(inputDatasetStream));
			result = processCsv(csvReader);
		} catch (IOException ex) {
			LOG.error("Failed to parse file {}! An error occured: {}",
					inputDatasetStream, ex);
			throw new RuntimeException(ex);
		}

		assert result != null: "Should have failed with an exception!";
		LOG.info("Finished parsing file {}.", inputDatasetStream);
		return result;
	}

	private int numberOfLeads(String[] line) {
		int numberOfLeadSpecificEntries = line.length - GENERAL_DATA_SIZE;
		int numberOfLeads = numberOfLeadSpecificEntries / ECG_LEAD_DATA_SIZE;
		return numberOfLeads;
	}

	private EcgData processCsv(CSVReader csvReader) throws IOException {
		LOG.info("Processing CSV file...");
		EcgData ecgData = new EcgData();
		
		String[] header = csvReader.readNext();
		assert header != null : "CSV format should always have headers!";

		String[] line = null;
		while ((line = csvReader.readNext()) != null) {
			List<String> cells = Arrays.asList(line);
			Iterator<String> cellIterator = cells.iterator();
			int numberOfLeads = numberOfLeads(line);
			LOG.debug("Number of leads in current line: {}", numberOfLeads);
			
			try {
				GeneralEcgData generalData = parseGeneralData(cellIterator);
				LOG.debug("General data: {}" + generalData);
				EcgPatientData patientValues = parseLeadEntries(cellIterator, numberOfLeads, generalData);
				ecgData.add(patientValues);
			} catch (NoSuchElementException ex) {
				LOG.error("Invalid CSV file format!");
			}
		}
		
		return ecgData;
	}

	private EcgLead getNthLead(int index) {
		EcgLead result = null;
		for (EcgLead lead : EcgLead.values()) {
			if (lead.getIndex() == index) {
				result = lead;
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("No ECG lead with index of "
					+ index + "!");
		}

		return result;
	}

	private EcgPatientData parseLeadEntries(Iterator<String> cells,	int numberOfLeads, GeneralEcgData generalData) {
		EcgPatientData leadValues = new EcgPatientData(generalData.getMedicalTestId(), null);

		for (int currentLeadIndex = 0; currentLeadIndex < numberOfLeads; ++currentLeadIndex) {
			EcgLead lead = getNthLead(currentLeadIndex);
			LOG.debug("Processing entries for lead: {}", lead);
			List<String> leadEntries = traversCurrentLeadCells(cells);
			EcgLeadValue leadValue = parseEcgLeadValue(lead, leadEntries);
			leadValues.add(leadValue);
		}

		return leadValues;
	}

	private List<String> traversCurrentLeadCells(Iterator<String> cells) {
		int numberOfCellsPerLead = ECG_LEAD_DATA_SIZE;
		List<String> leadCells = new ArrayList<>(numberOfCellsPerLead);
		
		LOG.debug("Advancing CSV line entries by {}", numberOfCellsPerLead);
		for (int valueIndex = 0; valueIndex < numberOfCellsPerLead; ++valueIndex) {
			String cell = cells.next();
			leadCells.add(cell);
		}

		return leadCells;
	}

	private EcgLeadValue parseEcgLeadValue(EcgLead lead,
			List<String> leadEntries) {
		Objects.requireNonNull(lead, "Must specify a valid lead!");
		Objects.requireNonNull(leadEntries,
				"Must specify a valid lead entry collection!");

		if (leadEntries.isEmpty()) {
			throw new IllegalArgumentException(
					"Lead entries must contain values!");
		}
		if (leadEntries.size() != ECG_LEAD_DATA_SIZE) {
			throw new IllegalArgumentException("Lead entries contain "
					+ leadEntries.size() + "number of values instead of "
					+ ECG_LEAD_DATA_SIZE + "!");
		}

		EcgLeadValue ecgLeadValue = new EcgLeadValue(lead);
		for (EcgProperty ecgProperty : EcgProperty.values()) {
			int valueIndex = ecgProperty.getIndex();
			String rawValue = leadEntries.get(valueIndex);
			LOG.debug("Processing ECG property '{}'.", ecgProperty);
			double ecgPropertyValue = Utility.parseValue(rawValue);
			ecgLeadValue.setValue(ecgProperty, ecgPropertyValue);
		}

		return ecgLeadValue;
	}

	private GeneralEcgData parseGeneralData(Iterator<String> cells) throws IOException {
		GeneralEcgData data = new GeneralEcgData();
		
		String medicalTestId = cells.next();
		data.setMedicalTestId(medicalTestId);
		
		String heartBeat = cells.next();
		data.setHeartBeat(heartBeat);
		
		String qrsIntervalMean = cells.next();
		data.setQrsIntervalMean(qrsIntervalMean);
		
		String qrsAxis = cells.next();
		data.setQrsAxis(qrsAxis);
		
		String qrsAxis3D = cells.next();
		data.setQrsAxis3D(qrsAxis3D);
		
		String intervalV1P1 = cells.next();
		data.setIntervalV1P1(intervalV1P1);
		
		String intervalV1P2 = cells.next();
		data.setIntervalV1P2(intervalV1P2);
		
		String amplitudeV1P1 = cells.next();
		data.setAmplitudeV1P1(amplitudeV1P1);
		
		String amplitudeV1P2 = cells.next();
		data.setAmplitudeV1P2(amplitudeV1P2);
		
		String areaV1P1 = cells.next();
		data.setAreaV1P1(areaV1P1);
		
		String areaV1P2 = cells.next();
		data.setAreaV1P2(areaV1P2);
		
		return data;
	}

}