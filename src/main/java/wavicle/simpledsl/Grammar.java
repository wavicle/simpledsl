package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.List;

public class Grammar {

	private List<Intent> intents = new ArrayList<>();

	public void addIntent(Intent intent) {
		intents.add(intent);
	}

	public List<Intent> getIntents() {
		return intents;
	}

	public ComprehensionResult comprehend(String inputSentence) {
		for (Intent intent : intents) {
			IntentMatchResult matchResult = intent.match(inputSentence);
			if (matchResult != null) {
				ComprehensionResult result = new ComprehensionResult();
				result.setIntentName(intent.getName());
				result.setPlaceValuesByName(matchResult.getPlaceValuesByName());
				return result;
			}
		}
		return null;
	}
}
