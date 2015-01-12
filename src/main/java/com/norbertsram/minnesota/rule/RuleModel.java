package com.norbertsram.minnesota.rule;

import static com.google.common.base.Objects.toStringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Objects.ToStringHelper;
import com.norbertsram.minnesota.ontology.mapper.entity.RuleEntity;

public class RuleModel {

	private final RuleEntity type;
	private final List<RuleProperty> properties;

	public RuleModel(RuleEntity identifier) {
		this.type = Objects.requireNonNull(identifier);
		properties = new ArrayList<>();
	}

	public RuleModel addProperty(RuleProperty property) {
		Objects.requireNonNull(property);

		properties.add(property);
		return this;
	}

	public RuleEntity getType() {
		return type;
	}

	public List<RuleProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	@Override
	public String toString() {
		ToStringHelper ts = toStringHelper(getClass());
		ts.add("type", type);
		for (RuleProperty property : properties) {
			ts.addValue(property);
		}

		return ts.toString();
	}
}
