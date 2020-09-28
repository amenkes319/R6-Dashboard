package main.java.undo;

public interface Undoable
{
	void undo();
	void redo();
	Undoable copy();
}
