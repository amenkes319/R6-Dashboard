package dashboard.java.actions;

import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.Node;

public class DeleteNodeAction implements Action, Undoable
{
	private Node node;
	
	public DeleteNodeAction()
	{
		reset();
	}
	
	public DeleteNodeAction(DeleteNodeAction removeNodeAction)
	{
		this.node = removeNodeAction.getNode();
	}
	
	public DeleteNodeAction(Node node)
	{
		this.node = node;
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public void setNode(Node node)
	{
		this.node = node;
	}

	@Override
	public void undo()
	{
		Global.maker.getCurrentPane().getChildren().add(node);
	}

	@Override
	public void redo()
	{
		Global.maker.getCurrentPane().getChildren().remove(node);
	}

	@Override
	public Undoable copy()
	{
		return new DeleteNodeAction(this);
	}

	@Override
	public void execute()
	{
		Global.maker.getCurrentPane().getChildren().remove(node);
	}

	@Override
	public boolean canExecute()
	{
		return true;
	}

	@Override
	public void reset()
	{
		this.node = null;
	}

}
