package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

/**
 * Additional tests for SimpleDSL. See DslInterpreterTestSuite if you are interested
 * in testing the overall functionality.
 * 
 * @author Shashank Araokar
 *
 */
public class AdditionalTestSuite {

	/**
	 * Verifies that:
	 * 
	 * 1. MutableIntent names must not be null
	 * 
	 * 2. MutableIntent names must not have whitespaces
	 */
	@Test
	public void intent_must_have_valid_name() {
		MutableIntent intent = new MutableIntent();
		DslInterpreter dslInterpreter = new DslInterpreter();

		intent.setName(null);
		assertThrows(() -> {
			dslInterpreter.addIntent(intent);
		}, e -> {
			assertEquals("The intent name must not be blank.", e.getMessage());
		});
		assertTrue(true);

		intent.setName(" my intent 		\n");
		assertThrows(() -> {
			dslInterpreter.addIntent(intent);
		}, e -> {
			assertEquals("The intent name must not have whitespaces.", e.getMessage());
		});
		assertTrue(true);
	}

	/**
	 * Verifies that if two intents with the same name are added to an interpreter,
	 * an exception is thrown.
	 */
	@Test
	public void intent_name_must_be_unique() {
		MutableIntent intent1 = new MutableIntent();
		intent1.setName("myintent");
		MutableIntent intent2 = new MutableIntent();
		intent2.setName("myintent");

		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(intent1);

		assertThrows(() -> {
			dslInterpreter.addIntent(intent2);
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
