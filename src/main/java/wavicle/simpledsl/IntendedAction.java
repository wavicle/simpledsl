package wavicle.simpledsl;

import java.util.Map;

public interface IntendedAction {

	Object execute(ComprehensionResult result, Map<String, Object> context);
}
