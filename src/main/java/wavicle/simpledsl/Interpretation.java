package wavicle.simpledsl;

import java.util.Map;

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

	public String getSlotValue(String name) {
		return slotValuesByName.get(name);
	}

}