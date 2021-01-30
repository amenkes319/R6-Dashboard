package dashboard.java.undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoCollector
{
	public static final UndoCollector INSTANCE = new UndoCollector();

	private Deque<Undoable> undo;
	private Deque<Undoable> redo;

	private int sizeMax;

	private UndoCollector()
	{
		undo = new ArrayDeque<>();
		redo = new ArrayDeque<>();
		sizeMax = 50;
	}

	/**
	 * Adds an undoable object to the collector.
	 * 
	 * @param undoable The undoable object to add.
	 */
	public void add(Undoable undoable)
	{
		if (undoable != null && sizeMax > 0)
		{
			if (undo.size() == sizeMax)
			{
				undo.removeLast();
			}

			undo.push(undoable.copy());
			redo.clear(); /* The redoable objects must be removed. */
		}
	}

	/**
	 * Undoes the last undoable object.
	 */
	public void undo()
	{
		if (!undo.isEmpty())
		{
			Undoable undoable = undo.pop();
			undoable.undo();
			redo.push(undoable);
		}
	}

	/**
	 * Redoes the last undoable object.
	 */
	public void redo()
	{
		if (!redo.isEmpty())
		{
			Undoable undoable = redo.pop();
			undoable.redo();
			undo.push(undoable);
		}
	}

	/**
	 * @return The last undoable object or null.
	 */
	public Undoable getLastUndo()
	{
		return undo.isEmpty() ? null : undo.peek();
	}

	/**
	 * @return The last redoable object or null.
	 */
	public Undoable getLastRedo()
	{
		return redo.isEmpty() ? null : redo.peek();
	}

	/**
	 * @param max The max number of saved undoable objects. Must be great than 0.
	 */
	public void setSizeMax(int max)
	{
		if (max >= 0)
		{
			for (int i = 0, nb = undo.size() - max; i < nb; i++)
			{
				undo.removeLast();
			}
			this.sizeMax = max;
		}
	}
}