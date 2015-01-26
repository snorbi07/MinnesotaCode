package com.norbertsram.minnesota.utility;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import au.com.bytecode.opencsv.CSVWriter;

import com.norbertsram.ecgapi.EcgReader;
import com.norbertsram.ecgapi.model.EcgData;
import com.norbertsram.ecgapi.model.EcgLeadValue;
import com.norbertsram.ecgapi.model.EcgPatientData;
import com.norbertsram.minnesota.expsys.MinnesotaExpertSystemReasoner;
import com.norbertsram.minnesota.io.csv.CsvParser;
import com.norbertsram.minnesota.ontology.MinnesotaOntologyReasoner;
import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;
import com.norbertsram.minnesota.rule.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: refactor to proper CLI application
public class DiagnosticApplication {

	private static final DecimalFormat decimalFormat = new DecimalFormat("#.####");

	// TODO(snorbi07): make it dynamic by reading file names from 'dataset' folder
	private static final String[] datasets = new String[]{"twa.csv", "incart.csv", "ptb.csv"};
	
	private static final String[] HEADERS =
			new String[]{
					"Medical Test Id", "Rule Id", "Classic", "Type-Reduced Aggregation", "Interval Distance Based Reduction",
					"Parameter 1 - ECG Lead", "Parameter 1 - Waveform type", "Parameter 1 - Waveform value",
					"Parameter 2 - ECG Lead", "Parameter 2 - Waveform type", "Parameter 2 - Waveform value"
			};

	private static final int ROW_SIZE = HEADERS.length;

	private static final Logger LOG = LoggerFactory.getLogger(DiagnosticApplication.class);

	public static void main(String[] args) {
		LOG.info("Starting DiagnosticResultComparisonApplication...");

		for (String datasetName : datasets) {
			InputStream dataset = loadDataset(datasetName);
			LOG.info("DiagnosticResultComparisonApplication processing dataset file: " + datasetName);
			String resultFilename = "result-" + datasetName;
			
			EcgData ecgData = parseDataset(dataset);
			EcgData uniquelyIdentifiedEcgData = assureUniqueIds(ecgData);
			List<List<String>> rows = processEcgData(uniquelyIdentifiedEcgData);
			try {
				writeResults(rows, resultFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOG.info("DiagnosticResultComparisonApplication finished!");
	}

	// In the available datasets there are multiple samples for each patient. In order to ease debugging and
	// improve diagnostic efficiency analysis we tag each entry with a unique counter (sample identifier)
	// Not pretty however it gets the job done!
	private static EcgData assureUniqueIds(EcgData ecgData) {
		Map<String, Integer> patientIds = new HashMap<>();
		Collection<EcgPatientData> data = ecgData.getData();
		EcgData uniqueIdEcgData = new EcgData();
		for (EcgPatientData patient : data) {
			String patientId = patient.getPatientId();
			int uniqueCounter = 0;

			if (patientIds.containsKey(patientId)) {
				uniqueCounter = patientIds.get(patientId);
			}

			String uniqueId = patientId + " (Sample " + uniqueCounter + ")";

			patientIds.put(patientId, uniqueCounter + 1);

			EcgPatientData uniqueIdPatientData = new EcgPatientData(uniqueId, patient.getDescription());

			copyEcgPatientData(patient, uniqueIdPatientData);
			uniqueIdEcgData.add(uniqueIdPatientData);
		}

		return uniqueIdEcgData;
	}

	private static void copyEcgPatientData(EcgPatientData from, EcgPatientData to) {
		List<EcgLeadValue> data = from.getData();
		for (EcgLeadValue value : data) {
			to.add(value);
		}
	}

	private static void writeResults(List<List<String>> rows, String outputFilename) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(outputFilename));
		writer.writeNext(HEADERS);
		for (List<String> row : rows) {
			String[] rowItems = row.toArray(new String[row.size()]);
			writer.writeNext(rowItems);
		}
		
		writer.close();
	}

	public static List<List<String>> processEcgData(EcgData data) {
		List<List<String>> allRows = new ArrayList<>(data.getData().size());
 		for(EcgPatientData patient : data.getData()) {
			List<List<String>> patientRows = processPatientData(patient);
			allRows.addAll(patientRows);
		}
 		
 		return allRows;
	}
	
	public static List<List<String>> processPatientData(EcgPatientData patientData) {
		Collection<RuleModel> rules = MinnesotaOntologyReasoner.processPatientData(patientData);
		List<List<String>> rows = new ArrayList<>(rules.size());
		
		String patientId = patientData.getPatientId();
		
		for (RuleModel rule : rules) {
			String ruleIdentifier = rule.getType().toString();
			boolean classicResult = MinnesotaExpertSystemReasoner.infer(rule);
			List<Double> fuzzyResults = inferRuleModel(rule);
			List<String> description = buildResultDescriptionColumns(rule);
			List<String> row = buildResultRow(patientId, ruleIdentifier, classicResult, fuzzyResults, description);
			rows.add(row);
		}
		
		return rows;
	}
	
	private static List<String> buildResultDescriptionColumns(RuleModel rule) {
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getType().toString()).append("{");
		List<String> descriptionRows = new ArrayList<>();
		for (RuleProperty property : rule.getProperties()) {
			// which ECG lead does the property belong to
			descriptionRows.add(property.getEcgLead().toString());

			// name of the property itself
			descriptionRows.add(property.getProperty().toString());
			// the value of the property
			descriptionRows.add(String.valueOf(property.getCrispValue()));
        }

		return descriptionRows;
	}

	private static List<Double> inferRuleModel(RuleModel rule) {
		List<Double> results = new ArrayList<>();

		RuleResult typeReducedAggregation = MinnesotaOntologyReasoner.typeReducedAggregation(rule);
		RuleResult distanceBasedReduction = MinnesotaOntologyReasoner.distanceBasedReduction(rule);

		results.add(typeReducedAggregation.getDegreeOfTruth());
		results.add(distanceBasedReduction.getDegreeOfTruth());

		return results;
	}
	
	private static List<String> buildResultRow(String patientId, String ruleId, boolean classicResult, List<Double> fuzzyResults, List<String> resultDescription) {
		List<String> row = new ArrayList<>(ROW_SIZE);
		row.add(patientId);
		row.add(ruleId);
		row.add(Boolean.toString(classicResult));

		for (double fuzzyResult : fuzzyResults) {
			String formatedResult = decimalFormat.format(fuzzyResult);
			row.add(formatedResult);
		}

		row.addAll(resultDescription);
		
		return row;
	}

	public static EcgData parseDataset(InputStream dataset) {
		EcgReader reader = new CsvParser(dataset);
		EcgData data = reader.read();
		return data;
	}
	
	public static InputStream loadDataset(String datasetName) {
		InputStream dataset = null;
		try {
			dataset = new FileInputStream("./dataset/" + datasetName);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find file name: " + datasetName + "\n" + e);
		}
		// FIXME: Related to issue #1, same bug as for ontology loading.
//		InputStream dataset = DiagnosticApplication.class.getResourceAsStream(datasetName);
		return dataset;
	}
	
}
