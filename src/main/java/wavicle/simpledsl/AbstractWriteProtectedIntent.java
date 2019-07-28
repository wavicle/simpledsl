package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class AbstractWriteProtectedIntent implements Intent {
	protected String name = this.getClass().getCanonicalName();

	protected List<Pattern> utterancePatterns = new ArrayList<>();

	private Map<String, String> regexByType = new HashMap<>();

	private StringSubstitutor stringSubstitutor = new StringSubstitutor(new StringLookup() {
		@Override
		public String lookup(String input) {
			String slotName;
			String regexType;
			if (!input.contains(":")) {
				slotName = StringUtils.trim(input);
				regexType = "xword";
			} else {
				String[] parts = input.split(":");
				if (parts.length != 2) {
					throw new IllegalArgumentException("Invalid pattern: " + input);
				}
				slotName = StringUtils.trim(parts[0]);
				regexType = StringUtils.trim(parts[1]);
			}
			if (!regexByType.containsKey(regexType)) {
				throw new IllegalArgumentException("No such slot type is known: " + regexType);
			} else if (StringUtils.isBlank(slotName)) {
				throw new IllegalArgumentException("The slot name must not be blank. Original input: " + input);
			}
			String innerRegex = regexByType.get(regexType);
			String finalRegex = String.format("(?<%s>%s)", slotName, innerRegex);
			return finalRegex;
		}
	});

	public AbstractWriteProtectedIntent() {
		regexByType.put("word", "\\w+");
		regexByType.put("xword", ".+");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wavicle.simpledsl.Intent#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wavicle.simpledsl.Intent#match(java.lang.String)
	 */
	@Override
	public IntentMatchResult match(String inputUtterance) {
		Validate.notNull(inputUtterance);
		for (Pattern utterancePattern : utterancePatterns) {
			IntentMatchResult result = match(inputUtterance, utterancePattern);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private IntentMatchResult match(String inputUtterance, Pattern pattern) {
		Matcher matcher = pattern.matcher(inputUtterance.replaceAll(" +", " "));
		IntentMatchResult result = null;
		if (matcher.find()) {
			result = new IntentMatchResult();
			List<String> groupNames = pattern.groupNames();
			for (String groupName : groupNames) {
				String groupValue = matcher.group(groupName);
				result.setPlaceValue(groupName, groupValue);
			}
		}
		return result;
	}

	protected void addRawSampleUtterances(String... utterances) {
		for (String utterance : utterances) {
			Pattern pattern = Pattern.compile(utterance, Pattern.CASE_INSENSITIVE);
			utterancePatterns.add(pattern);
		}
	}

	protected void addSampleUtterances(String... utterances) {
		for (String utterance : utterances) {
			String rawUtterance = stringSubstitutor.replace(utterance);
			Pattern pattern = Pattern.compile(rawUtterance, Pattern.CASE_INSENSITIVE);
			utterancePatterns.add(pattern);
		}
	}

}
