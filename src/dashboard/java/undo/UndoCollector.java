package dashboard.java.undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoCollector
{
	public static final UndoCollector INSTANCE = new UndoCollector();

	private Deque<Undoable> undo;
	private Deque<Undoable> redo;

	private final int MAX_SIZE;

	private UndoCollector()
	{
		undo = new ArrayDeque<>();
		redo = new ArrayDeque<>();
		MAX_SIZE = 1000;
	}
	
	public void add(Undoable undoable)
	{
		if (undoable != null && MAX_SIZE > 0)
		{
			if (undo.size() == MAX_SIZE)
			{
				undo.removeLast();
			}

			undo.push(undoable.copy());
			redo.clear();
		}
	}

	public void undo()
	{
		if (!undo.isEmpty())
		{
			Undoable undoable = undo.pop();
			undoable.undo();
			redo.push(undoable);
		}
	}

	public void redo()
	{
		if (!redo.isEmpty())
		{
			Undoable undoable = redo.pop();
			undoable.redo();
			undo.push(undoable);
		}
	}
	
	public Undoable getLastUndo()
	{
		return undo.isEmpty() ? null : undo.peek();
	}

	public Undoable getLastRedo()
	{
		return redo.isEmpty() ? null : redo.peek();
	}
}