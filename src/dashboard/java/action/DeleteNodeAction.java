package dashboard.java.action;

import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.image.ImageView;

public class DeleteNodeAction implements Action, Undoable
{
	private ImageView node;
	
	public DeleteNodeAction()
	{
		reset();
	}
	
	public DeleteNodeAction(DeleteNodeAction removeNodeAction)
	{
		this.node = removeNodeAction.getNode();
	}
	
	public DeleteNodeAction(ImageView node)
	{
		this.node = node;
	}
	
	public ImageView getNode()
	{
		return node;
	}
	
	public void setNode(ImageView node)
	{
		this.node = node;
	}

	@Override
	public void undo()
	{
		Global.maker.getCurrentPane().getChildren().addAll(Global.maker.getBorder(node), node);
	}

	@Override
	public void redo()
	{
		Global.maker.getCurrentPane().getChildren().removeAll(Global.maker.getBorder(node), node);
	}

	@Override
	public Undoable copy()
	{
		return new DeleteNodeAction(this);
	}

	@Override
	public void execute()
	{
		Global.maker.getCurrentPane().getChildren().removeAll(Global.maker.getBorder(node), node);
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
