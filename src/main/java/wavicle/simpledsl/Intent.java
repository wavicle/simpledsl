package wavicle.simpledsl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class Intent {
	private String name;

	private String regex;

	private Pattern pattern;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		this.regex = regex;
	}

	public IntentMatchResult match(String inputSentence) {
		Validate.notNull(inputSentence);
		Matcher matcher = pattern.matcher(inputSentence.replaceAll(" +", " "));
		IntentMatchResult result = null;
		if (matcher.find()) {
			result = new IntentMatchResult();
			List<Map<String, String>> namedGroups = matcher.namedGroups();
			for (Map<String, String> namedGroup : namedGroups) {
				for (String groupName : namedGroup.keySet()) {
					String groupValue = namedGroup.get(groupName);
					result.setPlaceValue(groupName, groupValue);
				}
			}
		}
		return result;
	}

}