package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

/**
 * Additional tests for SimpleDSL. See SimpleDSLTestSuite if you are interested
 * in testing the overall functionality.
 * 
 * @author Shashank Araokar
 *
 */
public class AdditionalTestSuite {

	/**
	 * Verifies that:
	 * 
	 * 1. Intent names must not be null
	 * 
	 * 2. Intent names must not have whitespaces
	 */
	@Test
	public void intent_must_have_valid_name() {
		Intent intent = new Intent();
		Grammar grammar = new Grammar();

		intent.setName(null);
		assertThrows(() -> {
			grammar.addIntent(intent);
		}, e -> {
			assertEquals("The intent name must not be blank.", e.getMessage());
		});
		assertTrue(true);

		intent.setName(" my intent 		\n");
		assertThrows(() -> {
			grammar.addIntent(intent);
		}, e -> {
			assertEquals("The intent name must not have whitespaces.", e.getMessage());
		});
		assertTrue(true);
	}

	/**
	 * Verifies that if two intents with the same name are added to a grammar, an
	 * exception is thrown.
	 */
	@Test
	public void intent_name_must_be_unique() {
		Intent intent1 = new Intent();
		intent1.setName("myintent");
		Intent intent2 = new Intent();
		intent2.setName("myintent");

		Grammar grammar = new Grammar();
		grammar.addIntent(intent1);

		assertThrows(() -> {
			grammar.addIntent(intent2);
		}, e -> {
			assertEquals("An intent with name: myintent has already been added.", e.getMessage());
		});

	}

	public void assertThrows(Runnable task, Consumer<Exception> exceptionValidator) {
		try {
			task.run();
			throw new AssertionError("No exception was thrown when it was expected.");
		} catch (Exception e) {
			exceptionValidator.accept(e);
		}
	}

}
