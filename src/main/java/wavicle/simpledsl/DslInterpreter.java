package wavicle.simpledsl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class DslInterpreter {

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

	public Optional<Interpretation> interpret(String inputSentence) {
		for (Intent intent : intentsByName.values()) {
			IntentMatchResult matchResult = intent.match(StringUtils.trimToEmpty(inputSentence));
			if (matchResult != null) {
				Map<String, String> placeValuesByName = matchResult.getPlaceValuesByName();
				Map<String, String> slotValuesByName = buildSlotValuesByName(intent, placeValuesByName);
				Interpretation result = new Interpretation(intent.getName(), slotValuesByName);
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}

	private Map<String, String> buildSlotValuesByName(Intent intent, Map<String, String> placeValuesByName) {
		Map<String, String> slotValuesByName = new HashMap<>();
		for (Map.Entry<String, String> entry : placeValuesByName.entrySet()) {
			String slotName = entry.getKey();
			String literalSlotValue = entry.getValue();
			slotValuesByName.put(slotName, literalSlotValue);
		}
		return slotValuesByName;
	}

}
