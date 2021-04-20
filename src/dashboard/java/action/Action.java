package dashboard.java.action;

public interface Action
{
	void execute();
	boolean canExecute();
	void reset();
}
