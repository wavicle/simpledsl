package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.List;

public class Grammar {

	private List<GrammarRule> rules = new ArrayList<>();

	public void addRule(GrammarRule rule) {
		rules.add(rule);
	}

	public List<GrammarRule> getRules() {
		return rules;
	}

	public ComprehensionResult comprehend(String inputSentence) {
		for (GrammarRule rule : rules) {
			RuleMatchResult matchResult = rule.match(inputSentence);
			if (matchResult != null) {
				ComprehensionResult result = new ComprehensionResult();
				result.setRuleName(rule.getName());
				result.setPlaceValuesByName(matchResult.getPlaceValuesByName());
				return result;
			}
		}
		return null;
	}
}
