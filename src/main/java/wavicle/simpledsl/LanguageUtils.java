package wavicle.simpledsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class LanguageUtils {

	private static final LevenshteinDistance LEV_DISTANCE = new LevenshteinDistance();

	private static final BiFunction<String, String, Integer> LEV_DISTANCE_FUNCTION = (s1, s2) -> LEV_DISTANCE.apply(s1,
			s2);

	private static final double DEFAULT_THRESHOLD = 0.2;

	public static class IdAndNames {
		private String id;
		private List<String> names;

		public IdAndNames(String id, List<String> names) {
			super();
			this.id = id;
			this.names = names;
		}

		public String getId() {
			return id;
		}

		public List<String> getNames() {
			return names;
		}
	}

	public static List<Pair<IdAndNames, Double>> resolve(String literal, Collection<IdAndNames> idAndNamesCollection) {
		return resolve(literal, idAndNamesCollection, DEFAULT_THRESHOLD, LEV_DISTANCE_FUNCTION);
	}

	public static List<Pair<IdAndNames, Double>> resolve(String literal, Collection<IdAndNames> idAndNamesCollection,
			double threshold) {
		return resolve(literal, idAndNamesCollection, threshold, LEV_DISTANCE_FUNCTION);
	}

	public static List<Pair<IdAndNames, Double>> resolve(String literal, Collection<IdAndNames> idAndNamesCollection,
			double threshold, BiFunction<String, String, Integer> distanceFunction) {
		List<Pair<IdAndNames, Double>> matches = new ArrayList<>();
		for (IdAndNames entry : idAndNamesCollection) {
			List<String> samples = entry.names;
			for (String sample : samples) {
				double distance = applyDistance(distanceFunction, literal, sample);
				double distFraction = distance / sample.length();
				if (distFraction <= threshold) {
					matches.add(Pair.of(entry, distance));
					break;
				}
			}
		}
		matches.sort((m1, m2) -> m1.getRight().compareTo(m2.getRight()));
		return matches;
	}

	private static double applyDistance(BiFunction<String, String, Integer> distanceFunction, String s1, String s2) {
		return distanceFunction.apply(StringUtils.normalizeSpace(s1).toLowerCase(),
				StringUtils.normalizeSpace(s2).toLowerCase());
	}
}
