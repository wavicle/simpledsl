package wavicle.simpledsl;

import java.util.HashMap;
import java.util.Map;

public class IntentMatchResult {
	private Map<String, String> placeValuesByName = new HashMap<>();

	public void setPlaceValue(String name, String value) {
		placeValuesByName.put(name, value);
	}

	public String getPlaceValue(String name) {
		return placeValuesByName.get(name);
	}

	public Map<String, String> getPlaceValuesByName() {
		return placeValuesByName;
	}
}
