package wavicle.simpledsl;

import java.util.HashMap;
import java.util.Map;

public class Interpretation {
	private String intentName;

	private Map<String, SlotValue> slotValuesByName = new HashMap<>();

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public SlotValue getSlotValue(String name) {
		return slotValuesByName.get(name);
	}

	public void setSlotValuesByName(Map<String, SlotValue> slotValuesByName) {
		this.slotValuesByName = slotValuesByName;
	}
}
