package com.norbertsram.minnesota.expsys;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;
import com.norbertsram.minnesota.rule.RuleModel;

public class MinnesotaExpertSystemReasoner {

	private static Map<RuleEntity, RuleHandler> handlers;
	static {
		handlers = new ImmutableMap.Builder<RuleEntity, RuleHandler>()
				.put(RuleEntity.RULE_1_1_1, Rule1_1_1.INSTANCE)
				.put(RuleEntity.RULE_1_1_2, Rule1_1_2.INSTANCE)
				.put(RuleEntity.RULE_1_1_3, Rule1_1_3.INSTANCE)
				.put(RuleEntity.RULE_1_2_1, Rule1_2_1.INSTANCE)
				.put(RuleEntity.RULE_1_2_2, Rule1_2_2.INSTANCE)
				.put(RuleEntity.RULE_1_2_6, Rule1_2_6.INSTANCE)
				.put(RuleEntity.RULE_1_3_1, Rule1_3_1.INSTANCE)
				.put(RuleEntity.RULE_1_3_3, Rule1_3_3.INSTANCE)
				.put(RuleEntity.RULE_1_3_5, Rule1_3_5.INSTANCE)
				.build();
	}
	
	static public boolean infer(RuleModel rule) {
		Objects.requireNonNull(rule);
		RuleEntity type = rule.getType();
		RuleHandler ruleHandler = getRuleHandlerForType(type);
		boolean result = ruleHandler.infer(rule);
		return result;
	}

	private static RuleHandler getRuleHandlerForType(RuleEntity type) {
		Objects.requireNonNull(type);
		RuleHandler ruleHandler = handlers.get(type);
		
		if (ruleHandler == null) {
			throw new IllegalArgumentException("No valid handler specified for rule: " + type);
		}
	
		return ruleHandler;
	}
	
}
