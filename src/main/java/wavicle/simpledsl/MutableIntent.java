package wavicle.simpledsl;

public class MutableIntent extends AbstractWriteProtectedIntent {

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addRawSampleUtterances(String... utterances) {
		super.addRawSampleUtterances(utterances);
	}

	@Override
	public void addSampleUtterances(String... utterances) {
		super.addSampleUtterances(utterances);
	}

}
