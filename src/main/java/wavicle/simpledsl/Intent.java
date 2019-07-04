package wavicle.simpledsl;

public interface Intent {

	String getName();

	IntentMatchResult match(String inputUtterance);

}