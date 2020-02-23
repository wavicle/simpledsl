package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * Tests to demonstrate functionality in {@link DslExecutor}
 * 
 * @author Shashank Araokar
 *
 */
public class DslExecutorTestSuite {

	/**
	 * Verifies that once an interpretation is available, it can be fed to the
	 * interpreter to execute an 'action' associated with the matching intent.
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
	public void execution_action_upon_interpretation() {
		/** This intent 'sets' the value of a variable **/
		MutableIntent setIntent = new MutableIntent();
		setIntent.setName("set_varname_to_varvalue");
		setIntent.addRawSampleUtterances("set (?<varname>\\w+) to '(?<varvalue>.+)'");

		/** This intent 'gets' the value of a variable **/
		MutableIntent getIntent = new MutableIntent();
		getIntent.setName("get_varvalue_for_varname");
		getIntent.addRawSampleUtterances("get (?<varname>\\w+)");

		DslInterpreter dslInterpreter = new DslInterpreter();
		dslInterpreter.addIntent(setIntent);
		dslInterpreter.addIntent(getIntent);

		/** Next we create a DslExecutor to execute the input commands **/
		Map<String, Object> context = new HashMap<>();
		DslExecutor<Map<String, Object>, String> dslExecutor = new DslExecutor<>(context);
		dslExecutor.addAction(getIntent.getName(), new IntendedAction<Map<String, Object>, String>() {

			@Override
			public String execute(Interpretation result, Map<String, Object> context) {
				String varName = result.getSlotValue("varname");
				String varValue = (String) context.get(varName);
				return varValue;
			}
		});
		dslExecutor.addAction(setIntent.getName(), new IntendedAction<Map<String, Object>, String>() {

			@Override
			public String execute(Interpretation result, Map<String, Object> context) {
				String varName = result.getSlotValue("varname");
				String varValue = result.getSlotValue("varvalue");
				context.put(varName, varValue);
				return null;
			}
		});

		/** We use the DSL to set and then get the name in a given 'context' **/
		dslExecutor.execute(dslInterpreter.interpret("set name to 'Shashank Araokar'"));
		String returnedName = dslExecutor.execute(dslInterpreter.interpret("get name"));

		/** The returned value must match the name **/
		assertEquals("Shashank Araokar", returnedName);
		assertEquals("Shashank Araokar", context.get("name"));
	}

	/**
	 * Verifies that the 'supports' method in {@link DslExecutor} tells whether this
	 * executor supports action for the specified intent name. Also verifies that if
	 * an intent is not supported and execution is attempted, an exception is
	 * thrown.
	 */
	@Test
	public void basic_validations() {
		DslExecutor<Map<String, Object>, String> executor = new DslExecutor<Map<String, Object>, String>(
				new HashMap<>());

		/**
		 * Since we have specified not intended actions, this will always return false
		 **/
		assertFalse(executor.supportsIntent("myintent"));

		/**
		 * If we still attempt to execute, an illegal argument exception will be thrown
		 **/
		try {
			Interpretation interpretation = new Interpretation("myintent", Collections.singletonMap("name", "Albert"));
			executor.execute(interpretation);
			fail("This line should not be executed. Instead, an exception should be thrown.");
		} catch (Exception e) {
			assertEquals("No such intent is supported: myintent", e.getMessage());
		}
	}

}
