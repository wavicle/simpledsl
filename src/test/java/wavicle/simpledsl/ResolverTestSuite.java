package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * A test suite to demonstrate the {@link SlotResolver} capability in SimpleDSL.
 * 
 * @author linuxlite
 *
 */
public class ResolverTestSuite {

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
		intent.addSampleUtterances("I have lived in (?<cityName>\\w+) since (?<year>\\w+)");
		/** Add a slot resolver that makes the city name uppercase **/
		intent.addSlotResolver("cityName", new SlotResolver() {

			@Override
			public String resolve(String literal) {
				return literal.toUpperCase();
			}
		});

		/** Create a grammar just with one intent **/
		Grammar grammar = new Grammar();
		grammar.addIntent(intent);

		/** We pass an input where the city name is all lowercase **/
		ComprehensionResult result = grammar.comprehend("I have lived in boston since 2014");

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
		intent.addSampleUtterances("I live in (?<placeName>\\w+)");

		LevenshteinDistanceBasedSlotResolver slotResolver = new LevenshteinDistanceBasedSlotResolver();
		slotResolver.addSamples("Massachusetts", new HashSet<>(Arrays.asList("Mass", "MA")));
		intent.addSlotResolver("placeName", slotResolver);

		Grammar grammar = new Grammar();
		grammar.addIntent(intent);

		/** The exact value obviously matches **/
		assertEquals("Massachusetts",
				grammar.comprehend("I live in Massachusetts").getSlotValue("placeName").getResolved());

		/** Minor mis-spellings are okay **/
		assertEquals("Massachusetts",
				grammar.comprehend("I live in Masachusts").getSlotValue("placeName").getResolved());

		/** Another synonym works too **/
		assertEquals("Massachusetts", grammar.comprehend("I live in MA").getSlotValue("placeName").getResolved());

	}

}
