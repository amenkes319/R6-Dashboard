package stratmaker.java.actions;

public interface Action
{
	void execute();
	boolean canExecute();
	void reset();
}
