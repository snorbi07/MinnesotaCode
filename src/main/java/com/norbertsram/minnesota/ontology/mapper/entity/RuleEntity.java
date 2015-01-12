package com.norbertsram.minnesota.ontology.mapper.entity;

import com.norbertsram.minnesota.ontology.mapper.entity.OntologyPathProvider;

// TODO: refactor to RuleType and RuleEntity, where RuleType is an ontology independat repr.
public enum RuleEntity implements OntologyPathProvider {

	RULE_1_1_1("Rule1-1-1"), RULE_1_1_2("Rule1-1-2"), RULE_1_1_3("Rule1-1-3"), RULE_1_1_4(
			"Rule1-1-4"), RULE_1_1_5("Rule1-1-5"), RULE_1_1_6("Rule1-1-6"), RULE_1_1_7(
			"Rule1-1-7"),

	RULE_1_2_1("Rule1-2-1"), RULE_1_2_2("Rule1-2-2"), RULE_1_2_3("Rule1-2-3"), RULE_1_2_4(
			"Rule1-2-4"), RULE_1_2_5("Rule1-2-5"), RULE_1_2_6("Rule1-2-6"), RULE_1_2_8(
			"Rule1-2-8"),

	RULE_1_3_1("Rule1-3-1"), RULE_1_3_2("Rule1-3-2"), RULE_1_3_3("Rule1-3-3"), RULE_1_3_4(
			"Rule1-3-4"), RULE_1_3_5("Rule1-3-5"), RULE_1_3_6("Rule1-3-6"),

	RULE_2_1("Rule2-1"), RULE_2_2("Rule2-2"), RULE_2_3("Rule2-3"), RULE_2_4(
			"Rule2-4"), RULE_2_5("Rule2-5"),

	RULE_3_1("Rule3-1"), RULE_3_2("Rule3-2"), RULE_3_3("Rule3-3"), RULE_3_4(
			"Rule3-4"),

	RULE_4_1_1("Rule4-1-1"), RULE_4_1_2("Rule4-1-2"), RULE_4_2("Rule4-2"), RULE_4_3(
			"Rule4-3"), RULE_4_4("Rule4-4"),

	RULE_5_1("Rule5-1"), RULE_5_2("Rule5-2"), RULE_5_3("Rule5-3"), RULE_5_4(
			"Rule5-4"),

	RULE_6_1("Rule6-1"), RULE_6_2_1("Rule6-2-1"), RULE_6_2_2("Rule6-2-2"), RULE_6_2_3(
			"Rule6-2-3"), RULE_6_3("Rule6-3"), RULE_6_4_1("Rule6-4-1"), RULE_6_4_2(
			"Rule6-4-2"), RULE_6_5("Rule6-5"), RULE_6_6("Rule6-6"), RULE_6_8(
			"Rule6-8"),

	RULE_7_1_1("Rule7-1"), RULE_7_1_2("Rule7-1-2"), RULE_7_2_1("Rule7-2-1"), RULE_7_2_2(
			"Rule7-2-2"), RULE_7_3("Rule7-3"), RULE_7_4("Rule7-4"), RULE_7_5(
			"Rule7-5"), RULE_7_6("Rule7-6"), RULE_7_7("Rule7-7"), RULE_7_8(
			"Rule7-8"),

	RULE_8_1_1("Rule8-1-1"), RULE_8_1_2("Rule8-1-2"), RULE_8_1_3("Rule8-1-3"), RULE_8_1_4(
			"Rule8-1-4"), RULE_8_1_5("Rule8-1-5"), RULE_8_2_1("Rule8-2-1"), RULE_8_2_2(
			"Rule8-2-2"), RULE_8_2_3("Rule8-2-3"), RULE_8_2_4("Rule8-2-4"), RULE_8_3_1(
			"Rule8-3-1"), RULE_8_3_2("Rule8-3-2"), RULE_8_3_3("Rule8-3-3"), RULE_8_3_4(
			"Rule8-3-4"), RULE_8_4_1("Rule8-4-1"), RULE_8_4_2("Rule8-4-2"), RULE_8_5_1(
			"Rule8-5-1"), RULE_8_5_2("Rule8-5-2"), RULE_8_6_1("Rule8-6-1"), RULE_8_6_2(
			"Rule8-6-2"), RULE_8_6_3("Rule8-6-3"), RULE_8_6_4("Rule8-6-4"), RULE_8_7(
			"Rule8-7"), RULE_8_8("Rule8-8"), RULE_8_9("Rule8-9"),

	RULE_9_1("Rule9-1"), RULE_9_2("Rule9-2"), RULE_9_3("Rule9-3"), RULE_9_4_1(
			"Rule9-4-1"), RULE_9_4_2("Rule9-4-2"), RULE_9_5("Rule9-5"), RULE_9_8_1(
			"Rule9-8-1"), RULE_9_8_2("Rule9-8-2"), ;

	private final String path;

	RuleEntity(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return OntologyDescription.NAMESPACE + path;
	}

	@Override
	public String toString() {
		return path;
	}

	public static RuleEntity getRuleEntityForPath(String iriPath) {
		RuleEntity result = null;
		for (RuleEntity entity : RuleEntity.values()) {
			String entityPath = entity.getPath();
			if (entityPath.equals(iriPath)) {
				result = entity;
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("Missing rule entity type for: "
					+ iriPath);
		}

		return result;
	}
}
