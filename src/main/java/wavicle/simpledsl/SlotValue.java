package wavicle.simpledsl;

public class SlotValue {

	private final String literal;

	private final String resolved;

	public SlotValue(String literal, String resolved) {
		super();
		this.literal = literal;
		this.resolved = resolved;
	}

	public String getLiteral() {
		return literal;
	}

	public String getResolved() {
		return resolved;
	}

}
