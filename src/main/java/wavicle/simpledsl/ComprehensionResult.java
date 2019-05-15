package wavicle.simpledsl;

import java.util.HashMap;
import java.util.Map;

public class ComprehensionResult {
	private String intentName;

	private Map<String, String> placeValuesByName = new HashMap<>();

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public void setPlaceValue(String name, String value) {
		placeValuesByName.put(name, value);
	}

	public String getPlaceValue(String name) {
		return placeValuesByName.get(name);
	}

	public Map<String, String> getPlaceValuesByName() {
		return placeValuesByName;
	}

	public void setPlaceValuesByName(Map<String, String> placeValuesByName) {
		this.placeValuesByName = placeValuesByName;
	}
}
