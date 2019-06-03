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
public class SimpleDSLTestSuite {

	/**
	 * Verifies that when a grammar is created with a list of intents, it correctly
	 * identifies the intent when given an input and also returns matching literal
	 * place values.
	 */
	@Test
	public void basic_literal_match() {
		/** Define an intent with a certain syntax governed by the regex **/
		Intent intent1 = new Intent();
		intent1.setName("my_pname_is_pval");
		intent1.addSampleUtterance("My (?<propertyName>\\w+) is (?<propertyValue>\\w+)");
		intent1.addSampleUtterance("(?<propertyValue>\\w+) is my (?<propertyName>\\w+)");

		/** Another intent is defined with slightly different syntax **/
		Intent intent2 = new Intent();
		intent2.setName("the_pname_of_owner_is_pval");
		intent2.addSampleUtterance("The (?<propertyName>\\w+) of (?<ownerName>\\w+) is (?<propertyValue>\\w+)");

		/** Create a grammar and add these two intents to it **/
		Grammar grammar = new Grammar();
		grammar.addIntent(intent1);
		grammar.addIntent(intent2);

		/** Let's test the grammar! This first sentence won't match anything **/
		ComprehensionResult result;
		assertNull(grammar.comprehend("Use the key with the door"));

		/** This should match intent 2 (note that extra spaces are ignored) **/
		result = grammar.comprehend("  The capital    of Canada  is Ottawa ");
		assertEquals(intent2.getName(), result.getIntentName());
		assertEquals("capital", result.getSlotValue("propertyName").getLiteral());
		assertEquals("Ottawa", result.getSlotValue("propertyValue").getLiteral());

		/** And this should match intent 1 (note that it is also case-insensitive!) **/
		result = grammar.comprehend("mY NaME iS Shashank");
		assertEquals(intent1.getName(), result.getIntentName());
		assertEquals("NaME", result.getSlotValue("propertyName").getLiteral());
		assertEquals("Shashank", result.getSlotValue("propertyValue").getLiteral());

		/** There is more. This is intent 1 with a different style **/
		result = grammar.comprehend("Java is my language");
		assertEquals(intent1.getName(), result.getIntentName());
		assertEquals("language", result.getSlotValue("propertyName").getLiteral());
		assertEquals("Java", result.getSlotValue("propertyValue").getLiteral());
	}

}
