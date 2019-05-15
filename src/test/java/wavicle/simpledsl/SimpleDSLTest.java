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
	 * Verifies that when a grammar is created with a list of rules, it correctly
	 * identifies the rule when given an input and also returns matching place
	 * values.
	 */
	@Test
	public void basic() {
		/** Define a rule with a certain syntax governed by the regex **/
		GrammarRule rule1 = new GrammarRule();
		rule1.setName("my_pname_is_pval");
		rule1.setRegex("My (?<propertyName>\\w+) is (?<propertyValue>\\w+)");

		/** Another rule is defined with slightly different syntax **/
		GrammarRule rule2 = new GrammarRule();
		rule2.setName("the_pname_of_owner_is_pval");
		rule2.setRegex("The (?<propertyName>\\w+) of (?<ownerName>\\w+) is (?<propertyValue>\\w+)");

		/** Create a grammar and add these two rules to it **/
		Grammar grammar = new Grammar();
		grammar.addRule(rule1);
		grammar.addRule(rule2);

		/** Let's test the grammar! This first sentence won't match anything **/
		assertNull(grammar.comprehend("Use the key with the door"));

		/** This should match rule 2 **/
		ComprehensionResult result2 = grammar.comprehend("The capital of Canada is Ottawa");
		assertEquals(rule2.getName(), result2.getRuleName());
		assertEquals("capital", result2.getPlaceValue("propertyName"));
		assertEquals("Ottawa", result2.getPlaceValue("propertyValue"));

		/** And this should match rule 1 **/
		ComprehensionResult result1 = grammar.comprehend("My name is Shashank");
		assertEquals(rule1.getName(), result1.getRuleName());
		assertEquals("name", result1.getPlaceValue("propertyName"));
		assertEquals("Shashank", result1.getPlaceValue("propertyValue"));
	}

}
