package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class LanguageUtils {

	private static final LevenshteinDistance LEV_DISTANCE = new LevenshteinDistance();

	private static final BiFunction<String, String, Integer> LEV_DISTANCE_FUNCTION = (s1, s2) -> LEV_DISTANCE.apply(s1,
			s2);

	private static final double DEFAULT_THRESHOLD = 0.2;

	public static Optional<String> resolve(String literal, Map<String, Set<String>> samplesByExact) {
		return resolve(literal, samplesByExact, DEFAULT_THRESHOLD, LEV_DISTANCE_FUNCTION);
	}

	public static Optional<String> resolve(String literal, Map<String, Set<String>> samplesByExact, double threshold) {
		return resolve(literal, samplesByExact, threshold, LEV_DISTANCE_FUNCTION);
	}

	public static Optional<String> resolve(String literal, Map<String, Set<String>> samplesByExact, double threshold,
			BiFunction<String, String, Integer> distanceFunction) {
		List<Pair<String, Double>> matches = new ArrayList<>();
		for (Entry<String, Set<String>> entry : samplesByExact.entrySet()) {
			String exactValue = entry.getKey();
			Set<String> samples = entry.getValue();
			for (String sample : samples) {
				double distance = applyDistance(distanceFunction, literal, sample);
				double distFraction = distance / sample.length();
				if (distFraction <= threshold) {
					matches.add(Pair.of(exactValue, distance));
				}
			}
		}
		if (matches.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(Collections.min(matches, (m1, m2) -> m1.getRight().compareTo(m2.getRight())).getLeft());
		}
	}

	private static double applyDistance(BiFunction<String, String, Integer> distanceFunction, String s1, String s2) {
		return distanceFunction.apply(StringUtils.normalizeSpace(s1).toLowerCase(),
				StringUtils.normalizeSpace(s2).toLowerCase());
	}
}
