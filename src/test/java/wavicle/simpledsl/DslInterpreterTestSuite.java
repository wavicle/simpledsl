package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 * A suite of tests to demonstrate how the {@link DslInterpreter} works to
 * implement a simple domain specific language.
 * 
 * @author Shashank Araokar
 *
 */
public class DslInterpreterTestSuite {

	/**
	 * Verifies that when a interpreter is created with a list of intents, it
	 * correctly identifies the intent when given an input and also returns matching
	 * literal place values.
	 */
	@Test
	public void basic_literal_match() {
		/** Define an intent with a certain syntax governed by the regex **/
		MutableIntent intent1 = new MutableIntent();
		intent1.setName("my_pname_is_pval");
		intent1.addSampleUtterances("My ${propertyName} is ${propertyValue}", "${propertyValue} is my ${propertyName}");

		/** Another intent is defined with slightly different syntax **/
		MutableIntent intent2 = new MutableIntent();
		intent2.setName("the_pname_of_owner_is_pval");
		intent2.addSampleUtterances("The ${propertyName} of ${ownerName} is ${propertyValue}");

		/** Create a interpreter and add these two intents to it **/
		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent1);
		dslInterpreter.addIntent(intent2);

		/** Let's test the interpreter! This first sentence won't match anything **/
		Interpretation result;
		assertNull(dslInterpreter.interpret("Use the key with the door"));

		/** This should match intent 2 (note that extra spaces are ignored) **/
		result = dslInterpreter.interpret("  The capital    of Canada  is Ottawa ");
		assertEquals(intent2.getName(), result.getIntentName());
		assertEquals("capital", result.getSlotValue("propertyName"));
		assertEquals("Ottawa", result.getSlotValue("propertyValue"));

		/** And this should match intent 1 (note that it is also case-insensitive!) **/
		result = dslInterpreter.interpret("mY NaME iS Shashank");
		assertEquals(intent1.getName(), result.getIntentName());
		assertEquals("NaME", result.getSlotValue("propertyName"));
		assertEquals("Shashank", result.getSlotValue("propertyValue"));

		/** There is more. This is intent 1 with a different style **/
		result = dslInterpreter.interpret("Java is my language");
		assertEquals(intent1.getName(), result.getIntentName());
		assertEquals("language", result.getSlotValue("propertyName"));
		assertEquals("Java", result.getSlotValue("propertyValue"));
	}

}
