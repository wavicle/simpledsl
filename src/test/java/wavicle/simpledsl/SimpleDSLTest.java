package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 * A suite of tests to demonstrate how SimpleDSL works
 * 
 * @author Shashank Araokar
 *
 */
public class SimpleDSLTest {

	/**
	 * Verifies that when a grammar is created with a list of intents, it correctly
	 * identifies the intent when given an input and also returns matching place
	 * values.
	 */
	@Test
	public void basic() {
		/** Define an intent with a certain syntax governed by the regex **/
		Intent intent1 = new Intent();
		intent1.setName("my_pname_is_pval");
		intent1.setRegex("My (?<propertyName>\\w+) is (?<propertyValue>\\w+)");

		/** Another intent is defined with slightly different syntax **/
		Intent intent2 = new Intent();
		intent2.setName("the_pname_of_owner_is_pval");
		intent2.setRegex("The (?<propertyName>\\w+) of (?<ownerName>\\w+) is (?<propertyValue>\\w+)");

		/** Create a grammar and add these two intents to it **/
		Grammar grammar = new Grammar();
		grammar.addIntent(intent1);
		grammar.addIntent(intent2);

		/** Let's test the grammar! This first sentence won't match anything **/
		assertNull(grammar.comprehend("Use the key with the door"));

		/** This should match intent 2 (note that extra spaces are ignored) **/
		ComprehensionResult result2 = grammar.comprehend("  The capital    of Canada  is Ottawa ");
		assertEquals(intent2.getName(), result2.getIntentName());
		assertEquals("capital", result2.getPlaceValue("propertyName"));
		assertEquals("Ottawa", result2.getPlaceValue("propertyValue"));

		/** And this should match intent 1 (note that it is also case-insensitive!) **/
		ComprehensionResult result1 = grammar.comprehend("mY NaME iS Shashank");
		assertEquals(intent1.getName(), result1.getIntentName());
		assertEquals("NaME", result1.getPlaceValue("propertyName"));
		assertEquals("Shashank", result1.getPlaceValue("propertyValue"));
	}

}
