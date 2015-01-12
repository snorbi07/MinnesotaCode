package com.norbertsram.minnesota.expsys;

import com.norbertsram.minnesota.rule.RuleModel;

interface RuleHandler {

	public boolean infer(RuleModel rule);

}
