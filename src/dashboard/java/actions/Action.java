package dashboard.java.actions;

public interface Action
{
	void execute();
	boolean canExecute();
	void reset();
}
