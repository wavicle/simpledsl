package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

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
		MutableIntent intent1 = new MutableIntent();
		intent1.setName("my_pname_is_pval");
		intent1.addSampleUtterances("My (?<propertyName>\\w+) is (?<propertyValue>\\w+)",
				"(?<propertyValue>\\w+) is my (?<propertyName>\\w+)");

		/** Another intent is defined with slightly different syntax **/
		MutableIntent intent2 = new MutableIntent();
		intent2.setName("the_pname_of_owner_is_pval");
		intent2.addSampleUtterances("The (?<propertyName>\\w+) of (?<ownerName>\\w+) is (?<propertyValue>\\w+)");

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
	 * Verifies that once a comprehension result is available, it can be fed to the
	 * grammar to execute an 'action' associated with the matching intent.
	 * 
	 * In this test, we create a simple DSL that can set and get the value of a
	 * variable. The syntax is as follows:
	 * 
	 * - set name to 'Shashank Araokar'
	 * 
	 * - get name
	 * 
	 * The first command sets the name and the next one gets the name back.
	 * 
	 */
	@Test
	public void execution_action_upon_comprehension() {
		/** This intent 'sets' the value of a variable **/
		MutableIntent setIntent = new MutableIntent();
		setIntent.setName("set_varname_to_varvalue");
		setIntent.addSampleUtterances("set (?<varname>\\w+) to '(?<varvalue>.+)'");
		setIntent.setAction(new IntendedAction() {

			@Override
			public Object execute(ComprehensionResult result, Map<String, Object> context) {
				String varName = result.getSlotValue("varname").getResolved();
				String varValue = result.getSlotValue("varvalue").getResolved();
				context.put(varName, varValue);
				return null;
			}
		});

		/** This intent 'gets' the value of a variable **/
		MutableIntent getIntent = new MutableIntent();
		getIntent.setName("get_varvalue_for_varname");
		getIntent.addSampleUtterances("get (?<varname>\\w+)");
		getIntent.setAction(new IntendedAction() {

			@Override
			public Object execute(ComprehensionResult result, Map<String, Object> context) {
				String varName = result.getSlotValue("varname").getResolved();
				String varValue = (String) context.get(varName);
				return varValue;
			}
		});

		Grammar grammar = new Grammar();
		grammar.addIntent(setIntent);
		grammar.addIntent(getIntent);

		/** We use the DSL to set and then get the name in a given 'context' **/
		Map<String, Object> context = new HashMap<>();
		grammar.comprehendAndExecute("set name to 'Shashank Araokar'", context);
		Object returnedName = grammar.comprehendAndExecute("get name", context);

		/** The returned value must match the name **/
		assertEquals("Shashank Araokar", returnedName);

		/**
		 * In fact, the context will have this value too (that is what the context is
		 * used for)
		 **/
		assertEquals("Shashank Araokar", context.get("name"));
	}

}
