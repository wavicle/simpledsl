package wavicle.simpledsl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * An abstract class to resolve slot values based on the Levenshtein distance
 * between words that represent those values.
 * 
 * @author Shashank Araokar
 *
 */
public class LevenshteinDistanceBasedSlotResolver implements SlotResolver {

	private Map<String, Set<String>> samplesByObjectName = new HashMap<>();

	private LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

	private double maxDistanceFraction = 0.1;

	public void setMaxDistanceFraction(double ceiling) {
		this.maxDistanceFraction = ceiling;
	}

	public void addSamples(String exactValue, Set<String> samples) {
		Validate.notBlank(exactValue, "The exact value must not be null or blank.");
		Validate.notEmpty(samples, "At least one sample value must be provided.");
		samples.forEach(sample -> Validate.notBlank(sample, "None of the samples must be blank or null."));
		/** Make sure that the exact value is itself a sample value **/
		samplesByObjectName.put(exactValue, SetUtils.union(samples, Collections.singleton(exactValue)));
	}

	@Override
	public String resolve(String literal) {
		Validate.notBlank(literal, "The literal to be resolved must not be blank.");
		for (Map.Entry<String, Set<String>> entry : samplesByObjectName.entrySet()) {
			String exactValue = entry.getKey();
			Set<String> samples = entry.getValue();
			for (String sample : samples) {
				int distance = levenshteinDistance.apply(literal, sample);
				int threshold = (int) (sample.length() * maxDistanceFraction);
				if (distance <= threshold) {
					return exactValue;
				}
			}
		}
		return null;
	}

}
