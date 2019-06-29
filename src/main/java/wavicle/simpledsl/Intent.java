package wavicle.simpledsl;

public interface Intent {

	String getName();

	SlotResolver getSlotResolverByName(String slotName);

	IntendedAction getAction();

	IntentMatchResult match(String inputUtterance);

}