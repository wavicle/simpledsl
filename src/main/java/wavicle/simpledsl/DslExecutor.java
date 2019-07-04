package wavicle.simpledsl;

import java.util.HashMap;
import java.util.Map;

public class DslExecutor<Context, Output> {

	private Context context;

	private Map<String, IntendedAction<Context, Output>> intendedActions = new HashMap<>();

	public DslExecutor(Context context) {
		super();
		this.context = context;
	}

	public void addAction(String intentName, IntendedAction<Context, Output> action) {
		intendedActions.put(intentName, action);
	}

	public boolean supportsIntent(String intentName) {
		return intendedActions.containsKey(intentName);
	}

	public Output execute(Interpretation interpretation) {
		String intentName = interpretation.getIntentName();
		if (!intendedActions.containsKey(intentName)) {
			throw new IllegalArgumentException("No such intent is supported: " + intentName);
		}
		return intendedActions.get(intentName).execute(interpretation, context);
	}

}
