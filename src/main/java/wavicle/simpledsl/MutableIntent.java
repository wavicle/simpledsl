package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class MutableIntent implements Intent {
	private String name;

	private List<Pattern> utterancePatterns = new ArrayList<>();

	private Map<String, SlotResolver> slotResolversByName = new HashMap<>();

	private IntendedAction action;

	/*
	 * (non-Javadoc)
	 * 
	 * @see wavicle.simpledsl.Intent#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addSampleUtterances(String... utterances) {
		for (String utterance : utterances) {
			Pattern pattern = Pattern.compile(utterance, Pattern.CASE_INSENSITIVE);
			utterancePatterns.add(pattern);
		}
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

	public void addSlotResolver(String slotName, SlotResolver slotResolver) {
		slotResolversByName.put(slotName, slotResolver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wavicle.simpledsl.Intent#getSlotResolverByName(java.lang.String)
	 */
	@Override
	public SlotResolver getSlotResolverByName(String slotName) {
		return slotResolversByName.get(slotName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wavicle.simpledsl.Intent#getAction()
	 */
	@Override
	public IntendedAction getAction() {
		return action;
	}

	public void setAction(IntendedAction action) {
		this.action = action;
	}

}
