package com.norbertsram.minnesota.utility;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.ImmutableList;
import com.norbertsram.minnesota.common.Utility;

//TODO: refactor to proper CLI application
public class DiagnosticResultAnalyzer {

	private final static String[] resultFiles = new String[] { "incart.csv-result", "twa.csv-result", "ptb.csv-result"};

	public static void main(String[] args) {
		System.out.println("Starting diagnostic result analyzation...");
		try {
			for (String fileName : resultFiles) {
				System.out.println("Analyzing file: " + fileName);
				Classification classification = new Classification();
				List<DiagnosticResultEntry> data = loadDiagnosticResultFile(fileName);
				analyzeDiagnosticResultEntries(data, classification);
				writeClassificationFiles(classification, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Result analyzation finished!");
	}

	private static void writeClassificationFiles(Classification classification,	String fileName) throws IOException {
		CSVWriter inconsistentEntryWriter = new CSVWriter(new FileWriter(fileName + "-inconsistent"));
		CSVWriter borderlineEntryWriter = new CSVWriter(new FileWriter(fileName + "-borderline"));
		CSVWriter dominantFactorEntryWriter = new CSVWriter(new FileWriter(fileName + "-dominant"));

		writeCase(classification.getInconsistentCases(), inconsistentEntryWriter);
		inconsistentEntryWriter.flush();
		inconsistentEntryWriter.close();
		
		writeCase(classification.getBorderlineCases(), borderlineEntryWriter);
		borderlineEntryWriter.flush();
		borderlineEntryWriter.close();

		writeCase(classification.getDominantCases(), dominantFactorEntryWriter);
		dominantFactorEntryWriter.flush();
		dominantFactorEntryWriter.close();
	}

	private static void writeCase(List<DiagnosticResultEntry> entries, CSVWriter writer) {
		final int NUMBER_OF_COLUMNS = 6;
		String[] row = new String[NUMBER_OF_COLUMNS]; 
		for (DiagnosticResultEntry entry : entries) {
			// TODO: aarggh... magic numbers again
			row[0] = entry.getRuleId();
			row[1] = entry.getSampleId();
			row[2] = Boolean.toString(entry.getExpertSystemResult());
			row[3] = Double.toString(entry.getFuzzyMinOperatorResult());
			row[4] = Double.toString(entry.getFuzzyMaxOperatorResult());
			row[5] = entry.getDescription();
			writer.writeNext(row);
		}
	}
	
	private static List<String[]> analyzeDiagnosticResultEntries(List<DiagnosticResultEntry> entries, Classification classification) {
		for (DiagnosticResultEntry resultEntry : entries) {
			classifyDiagnosticResultEntry(resultEntry, classification);
		}
		return null;
	}

	private static void classifyDiagnosticResultEntry(DiagnosticResultEntry result,	Classification classification) {
		boolean classic = result.getExpertSystemResult();
		double fuzzyMin = result.getFuzzyMinOperatorResult();
		double fuzzyMax = result.getFuzzyMaxOperatorResult();
		
		if (isInconsistent(classic, fuzzyMin)) {
			classification.addInconsistentCase(result);
		}
		if (isBorderlineCase(classic, fuzzyMin)) {
			classification.addBorderlineCase(result);
		}
		if (hasDominantFactor(fuzzyMin, fuzzyMax)) {
			classification.addDominantCase(result);
		}
	}

	// fuzzy result not equal to expert system result
	private static boolean isInconsistent(boolean classicResult, double fuzzy) {
		final double TRUTH_TRESHOLD = 0.5;
		boolean isTrue = fuzzy > TRUTH_TRESHOLD;

		return classicResult != isTrue;
	}

	// the diagnostic results produced by the classic sys. compared to fuzzy
	// might match but can be flagged
	private static boolean isBorderlineCase(boolean classic, double fuzzy) {
		final double TRESHOLD = 0.1;
		double classicValue = classic ? 1.0 : 0.0;

		boolean isBorderline;
		if (classic) {
			isBorderline = (classicValue - TRESHOLD) >= fuzzy;
		}
		else {
			isBorderline = (classicValue + TRESHOLD) <= fuzzy;
		}

		return isBorderline;
	}

	private static boolean hasDominantFactor(double fuzzyMin, double fuzzyMax) {
		final double DOMINANT_TRESHOLD = 0.5;
		// one factor dominant
		boolean isDominant = Math.abs(fuzzyMin - fuzzyMax) >= DOMINANT_TRESHOLD;

		return isDominant;
	}

	private static List<DiagnosticResultEntry> loadDiagnosticResultFile(String fileName) throws IOException {
		FileReader fileReader = new FileReader(fileName);
		List<DiagnosticResultEntry> resultEntries = Collections.emptyList();
		
		try (CSVReader reader = new CSVReader(fileReader)) {
			String[] headers = reader.readNext();
			if (!isValidResultFileHeaderEntries(headers)) {
				throw new IllegalArgumentException("File:'" + fileName  + "' is corrupt or has an unsupported format!");
			}
			
			resultEntries = new ArrayList<>();
			String[] row = null;
			while ((row = reader.readNext()) != null) {
				DiagnosticResultEntry entry = parseResultFileRow(row);
				resultEntries.add(entry);
			}
		}
		
		return resultEntries;
	}
	
	static private DiagnosticResultEntry parseResultFileRow(String[] row) {
		// I hate magic numbers....... TODO for when I'll have time for this (never!)
		String sampleId = row[0];
		String ruleId = row[1];
		boolean expertSystemResult = Boolean.parseBoolean(row[2]);
		double fuzzyMinOperatorResult = Utility.parseValue(row[3]);
		double fuzzyMaxOperatorResult = Utility.parseValue(row[4]);
		String description = row[5];
		DiagnosticResultEntry entry = 
				new DiagnosticResultEntry(sampleId, ruleId, expertSystemResult, fuzzyMinOperatorResult, fuzzyMaxOperatorResult, description);
		return entry;
	}

	private static boolean isValidResultFileHeaderEntries(String[] headers) {
		final int EXPECTED_NUMBER_OF_ENTRIES = 5;
		return headers != null && headers.length == EXPECTED_NUMBER_OF_ENTRIES;
	}

	private static final class DiagnosticResultEntry {
		private final String sampleId;
		private final String ruleId;
		private final boolean expertSystemResult;
		private final double fuzzyMinOperatorResult;
		private final double fuzzyMaxOperatorResult;
		private final String description;

		public DiagnosticResultEntry(String sampleId, String ruleId,
				boolean expertSystemResult, double fuzzyMinOperatorResult,
				double fuzzyMaxOperatorResult, String description) {
			this.sampleId = sampleId;
			this.ruleId = ruleId;
			this.expertSystemResult = expertSystemResult;
			this.fuzzyMinOperatorResult = fuzzyMinOperatorResult;
			this.fuzzyMaxOperatorResult = fuzzyMaxOperatorResult;
			this.description = description;
		}

		public String getSampleId() {
			return sampleId;
		}

		public String getRuleId() {
			return ruleId;
		}

		public boolean getExpertSystemResult() {
			return expertSystemResult;
		}

		public double getFuzzyMinOperatorResult() {
			return fuzzyMinOperatorResult;
		}

		public double getFuzzyMaxOperatorResult() {
			return fuzzyMaxOperatorResult;
		}
		
		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return sampleId + ", " + ruleId + ", " + expertSystemResult + ", " + fuzzyMinOperatorResult + ", " + fuzzyMaxOperatorResult + ", " + description;
		}

	}

	private static final class Classification {
		private List<DiagnosticResultEntry> dominantCases;
		private List<DiagnosticResultEntry> inconsistentCases;
		private List<DiagnosticResultEntry> borderlineCases;

		public Classification() {
			dominantCases = new ArrayList<>();
			inconsistentCases = new ArrayList<>();
			borderlineCases = new ArrayList<>();
		}

		public void addBorderlineCase(DiagnosticResultEntry value) {
			borderlineCases.add(value);
		}

		public void addInconsistentCase(DiagnosticResultEntry value) {
			inconsistentCases.add(value);
		}

		public void addDominantCase(DiagnosticResultEntry value) {
			dominantCases.add(value);
		}
		
		public List<DiagnosticResultEntry> getDominantCases() {
			return ImmutableList.copyOf(dominantCases);
		}

		public List<DiagnosticResultEntry> getInconsistentCases() {
			return ImmutableList.copyOf(inconsistentCases);
		}
		
		public List<DiagnosticResultEntry> getBorderlineCases() {
			return ImmutableList.copyOf(borderlineCases);
		}
		
	}

}
