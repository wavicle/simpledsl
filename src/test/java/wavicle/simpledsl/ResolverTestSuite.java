package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * A test suite to demonstrate the {@link SlotResolver} capability in SimpleDSL.
 * 
 * @author linuxlite
 *
 */
public class ResolverTestSuite {

	/**
	 * Verifies that if there is not {@link SlotResolver} is defined for the
	 * (intent, slot) combination but one is defined at the intent level, it applies
	 * to any slot by default.
	 */
	@Test
	public void default_slot_resolver() {
		MutableIntent intent = new MutableIntent();
		intent.setName("testintent");
		intent.addSampleUtterances("I live in ${placeName}");

		LevenshteinDistanceBasedSlotResolver slotResolver = new LevenshteinDistanceBasedSlotResolver();
		slotResolver.setMaxDistanceFraction(0.2);
		Map<String, Set<String>> samplesMap = new HashMap<>();
		samplesMap.put("Massachusetts", new HashSet<>(Arrays.asList("Mass", "MA")));
		samplesMap.put("California", new HashSet<>(Arrays.asList("Cali", "CA")));
		slotResolver.setSampleSupplier(() -> samplesMap);

		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent);
		dslInterpreter.setDefaultSlotResolver(slotResolver);

		/** The exact value obviously matches **/
		assertEquals("Massachusetts",
				dslInterpreter.interpret("I live in Massachusetts").getSlotValue("placeName").getResolved());

	}

	/**
	 * Verifies that if there is not {@link SlotResolver} is defined for the
	 * (intent, slot) combination but one is defined at the intent level, it applies
	 * to any slot by default.
	 */
	@Test
	public void default_intent_level_slot_resolver() {
		MutableIntent intent = new MutableIntent();
		intent.setName("testintent");
		intent.addSampleUtterances("I live in ${placeName}");

		LevenshteinDistanceBasedSlotResolver slotResolver = new LevenshteinDistanceBasedSlotResolver();
		slotResolver.setMaxDistanceFraction(0.2);
		Map<String, Set<String>> samplesMap = new HashMap<>();
		samplesMap.put("Massachusetts", new HashSet<>(Arrays.asList("Mass", "MA")));
		samplesMap.put("California", new HashSet<>(Arrays.asList("Cali", "CA")));
		slotResolver.setSampleSupplier(() -> samplesMap);

		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent);
		dslInterpreter.setDefaultSlotResolverForIntent(intent.getName(), slotResolver);

		/** The exact value obviously matches **/
		assertEquals("Massachusetts",
				dslInterpreter.interpret("I live in Massachusetts").getSlotValue("placeName").getResolved());

	}

	/**
	 * Verifies that the SlotResolver interface can be used in 'sanitizing' or
	 * 'resolving' literal slot values. In this example, the literal slot value is
	 * just made all upper-case, but more complex resolution can be programmed as
	 * needed.
	 */
	@Test
	public void simple_slot_resolver() {
		/** Create a simple intent **/
		MutableIntent intent = new MutableIntent();
		intent.setName("myintent");
		intent.addRawSampleUtterances("I have lived in (?<cityName>\\w+) since (?<year>\\w+)");

		/** Create an interpreter just with one intent **/
		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent);
		/** Add a slot resolver that makes the city name uppercase **/
		dslInterpreter.setSlotResolverForIntentAndSlot(intent.getName(), "cityName", new SlotResolver() {

			@Override
			public String resolve(String literal) {
				return literal.toUpperCase();
			}
		});

		/** We pass an input where the city name is all lowercase **/
		Interpretation result = dslInterpreter.interpret("I have lived in boston since 2014");

		/**
		 * Verify that the literal match is still lower case, but the resolved value is
		 * all uppercase
		 **/
		SlotValue cityNameSlotValue = result.getSlotValue("cityName");
		assertEquals("boston", cityNameSlotValue.getLiteral());
		assertEquals("BOSTON", cityNameSlotValue.getResolved());

		/**
		 * Also verify that if no resolver is specified, the literal and resolved values
		 * are identical
		 **/
		SlotValue yearSlotValue = result.getSlotValue("year");
		assertEquals("2014", yearSlotValue.getLiteral());
		assertEquals("2014", yearSlotValue.getResolved());
	}

	/**
	 * Demonstrates how slots can be resolved using the
	 * {@link LevenshteinDistanceBasedSlotResolver}
	 */
	@Test
	public void levenshtein_distance_slot_resolver() {
		MutableIntent intent = new MutableIntent();
		intent.setName("testintent");
		intent.addRawSampleUtterances("I live in (?<placeName>\\w+)");

		LevenshteinDistanceBasedSlotResolver slotResolver = new LevenshteinDistanceBasedSlotResolver();
		slotResolver.setMaxDistanceFraction(0.2);
		Map<String, Set<String>> samplesMap = new HashMap<>();
		samplesMap.put("Massachusetts", new HashSet<>(Arrays.asList("Mass", "MA")));
		samplesMap.put("California", new HashSet<>(Arrays.asList("Cali", "CA")));
		slotResolver.setSampleSupplier(() -> samplesMap);

		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent);
		dslInterpreter.setSlotResolverForIntentAndSlot(intent.getName(), "placeName", slotResolver);

		/** The exact value obviously matches **/
		assertEquals("Massachusetts",
				dslInterpreter.interpret("I live in Massachusetts").getSlotValue("placeName").getResolved());

		/** Minor mis-spellings are okay **/
		assertEquals("Massachusetts",
				dslInterpreter.interpret("I live in Maschusetts").getSlotValue("placeName").getResolved());

		/** Another synonym works too **/
		assertEquals("Massachusetts", dslInterpreter.interpret("I live in MA").getSlotValue("placeName").getResolved());

		/** Another synonym works too **/
		assertEquals("California",
				dslInterpreter.interpret("I live in Calfornia").getSlotValue("placeName").getResolved());

		/**
		 * This one can't be resolved because it exceeds the maxDistanceFraction set
		 * above (0.2)
		 **/
		assertNull(dslInterpreter.interpret("I live in Calif").getSlotValue("placeName").getResolved());

	}

}
