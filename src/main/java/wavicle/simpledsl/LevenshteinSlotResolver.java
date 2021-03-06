package wavicle.simpledsl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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
public class LevenshteinSlotResolver implements SlotResolver {

	private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

	private final double maxDistanceFraction;

	private final Supplier<Map<String, Set<String>>> sampleSupplier;

	public LevenshteinSlotResolver(Supplier<Map<String, Set<String>>> sampleSupplier) {
		this(0.2, sampleSupplier);
	}

	public LevenshteinSlotResolver(double maxDistanceFraction, Supplier<Map<String, Set<String>>> sampleSupplier) {
		super();
		this.maxDistanceFraction = maxDistanceFraction;
		this.sampleSupplier = sampleSupplier;
	}

	@Override
	public String resolve(String literal) {
		Validate.notBlank(literal, "The literal to be resolved must not be blank.");
		String saneLiteral = literal.toLowerCase();
		Map<String, Set<String>> samplesByObjectName = sampleSupplier.get();
		for (Map.Entry<String, Set<String>> entry : samplesByObjectName.entrySet()) {
			String exactValue = entry.getKey();
			Set<String> samples = entry.getValue();
			for (String sample : SetUtils.union(samples, Collections.singleton(exactValue))) {
				int distance = levenshteinDistance.apply(saneLiteral, sample.toLowerCase());
				int threshold = (int) (sample.length() * maxDistanceFraction);
				if (distance <= threshold) {
					return exactValue;
				}
			}
		}
		return null;
	}

}
