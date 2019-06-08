package wavicle.simpledsl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Grammar {

	private Map<String, Intent> intentsByName = new HashMap<>();

	public void addIntent(Intent intent) {
		Validate.notNull(intent, "The intent must not be null.");
		Validate.notBlank(intent.getName(), "The intent name must not be blank.");
		Validate.isTrue(!StringUtils.containsWhitespace(intent.getName()),
				"The intent name must not have whitespaces.");
		if (intentsByName.containsKey(intent.getName())) {
			throw new IllegalArgumentException("An intent with name: " + intent.getName() + " has already been added.");
		}
		intentsByName.put(intent.getName(), intent);
	}

	public Collection<Intent> getIntents() {
		return intentsByName.values();
	}

	public ComprehensionResult comprehend(String inputSentence) {
		for (Intent intent : intentsByName.values()) {
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

	public Object comprehendAndExecute(String inputSentence, Map<String, Object> context) {
		ComprehensionResult comprehensionResult = comprehend(inputSentence);
		Object returnable = null;
		if (comprehensionResult != null) {
			Intent intent = intentsByName.get(comprehensionResult.getIntentName());
			IntendedAction action = intent.getAction();
			if (action != null) {
				returnable = action.execute(comprehensionResult, context);
			}
		}
		return returnable;
	}
}
