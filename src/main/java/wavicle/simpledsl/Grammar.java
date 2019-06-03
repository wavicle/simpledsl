package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				Map<String, String> placeValuesByName = matchResult.getPlaceValuesByName();
				Map<String, SlotValue> slotValuesByName = buildSlotValuesByName(intent, placeValuesByName);
				result.setSlotValuesByName(slotValuesByName);
				return result;
			}
		}
		return null;
	}

	private Map<String, SlotValue> buildSlotValuesByName(Intent intent, Map<String, String> placeValuesByName) {
		Map<String, SlotValue> slotValuesByName = new HashMap<>();
		for (Map.Entry<String, String> entry : placeValuesByName.entrySet()) {
			String slotName = entry.getKey();
			String literalSlotValue = entry.getValue();
			SlotResolver slotResolver = intent.getSlotResolverByName(slotName);
			String resolvedSlotValue = slotResolver == null ? literalSlotValue : slotResolver.resolve(literalSlotValue);
			SlotValue slotValue = new SlotValue(literalSlotValue, resolvedSlotValue);
			slotValuesByName.put(slotName, slotValue);
		}
		return slotValuesByName;
	}
}
