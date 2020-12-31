package wavicle.simpledsl;

import java.util.Map;
import java.util.Optional;

public class Interpretation {
	private final String intentName;

	private final Map<String, String> slotValuesByName;

	public Interpretation(String intentName, Map<String, String> slotValuesByName) {
		super();
		this.intentName = intentName;
		this.slotValuesByName = slotValuesByName;
	}

	public String getIntentName() {
		return intentName;
	}

	public Optional<String> getSlotValue(String name) {
		return Optional.ofNullable(slotValuesByName.get(name));
	}

}