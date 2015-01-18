package com.norbertsram.minnesota.utility;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

import com.norbertsram.ecgapi.EcgProperty;
import com.norbertsram.ecgapi.EcgReader;
import com.norbertsram.ecgapi.model.EcgData;
import com.norbertsram.ecgapi.model.EcgPatientData;
import com.norbertsram.flt.operator.Max;
import com.norbertsram.flt.operator.Min;
import com.norbertsram.flt.operator.Operator;
import com.norbertsram.minnesota.expsys.MinnesotaExpertSystemReasoner;
import com.norbertsram.minnesota.io.csv.CsvParser;
import com.norbertsram.minnesota.ontology.MinnesotaOntologyReasoner;
import com.norbertsram.minnesota.rule.RuleModel;
import com.norbertsram.minnesota.rule.RuleProperty;
import com.norbertsram.minnesota.rule.RuleResult;

// TODO: refactor to proper CLI application
public class DiagnosticApplication {

	private static final Operator[] fuzzyOperators = new Operator[]{Min.INSTANCE, Max.INSTANCE};
	
	private static final DecimalFormat decimalFormat = new DecimalFormat("#.####");
	
	private static final String[] datasets = new String[]{"twa.csv", "incart.csv", "ptb.csv"};
	
	private static final int ROW_SIZE = 6;
	
	private static final String[] headers = new String[]{"Medical Test Id", "Rule Id", "Classic", "Operator - Min", "Operator - Max"}; 
	
	public static void main(String[] args) {
		System.out.println("Starting DiagnosticResultComparisonApplication...");
		
		
		for (String datasetName : datasets) {
			InputStream dataset = loadDataset(datasetName);
			System.out.println("DiagnosticResultComparisonApplication processing dataset file: " + datasetName);
			String resultFilename = datasetName + "-result";
			
			EcgData ecgData = parseDataset(dataset);
			List<List<String>> rows = processEcgData(ecgData);
			try {
				writeResults(rows, resultFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("DiagnosticResultComparisonApplication finished!");
	}

	private static void writeResults(List<List<String>> rows, String outputFilename) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(outputFilename));
		writer.writeNext(headers);
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
			Map<Operator, Double> fuzzyResults = inferRuleModel(rule);
			String resultDescription = buildResultDescription(rule);
			List<String> row = buildResultRow(patientId, ruleIdentifier, classicResult, fuzzyResults, resultDescription);
			rows.add(row);
		}
		
		return rows;
	}
	
	private static String buildResultDescription(RuleModel rule) {
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getType().toString()).append("{");
        for (RuleProperty property : rule.getProperties()) {
            EcgProperty ecgProperty = property.getProperty();
            sb.append(ecgProperty.toString()).append("=").append(property.getCrispValue());
            sb.append(", ");
        }
        sb.append("}");

        return sb.toString();
    }

	private static Map<Operator, Double> inferRuleModel(RuleModel rule) {
		Map<Operator, Double> results = new HashMap<>(fuzzyOperators.length);
		
		for (Operator op : fuzzyOperators) {
			RuleResult result = MinnesotaOntologyReasoner.infer(rule, op);
			double degreeOfTruth = result.getDegreeOfTruth();
			results.put(op, degreeOfTruth);
		}
		
		return results;
	}
	
	private static List<String> buildResultRow(String patientId, String ruleId, boolean classicResult, Map<Operator, Double> fuzzyResults, String resultDescription) {
		List<String> row = new ArrayList<>(ROW_SIZE);
		row.add(patientId);
		row.add(ruleId);
		row.add(Boolean.toString(classicResult));
		
		for (Operator op : fuzzyOperators) {
			Double value = fuzzyResults.get(op);
			assert value != null;
			String formatedResult = decimalFormat.format(value);
			row.add(formatedResult);
		}
		
		row.add(resultDescription);
		
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
