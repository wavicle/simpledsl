package wavicle.simpledsl;

public interface IntendedAction<Context, Output> {

	Output execute(Interpretation interpretation, Context context);
}
