package dashboard.java.action;

import dashboard.java.global.Global;
import dashboard.java.node.ImageNode;
import dashboard.java.undo.Undoable;

public class AddNodeAction implements Action, Undoable
{
	private ImageNode imgNode;
	
	public AddNodeAction()
	{
		reset();
	}
	
	public AddNodeAction(AddNodeAction addNodeAction)
	{
		this.imgNode = addNodeAction.getImageNode();
	}

	public AddNodeAction(ImageNode imgNode)
	{
		this.imgNode = imgNode;
	}

	public ImageNode getImageNode()
	{
		return imgNode;
	}

	public void setImageNode(ImageNode imgNode)
	{
		this.imgNode = imgNode;
	}

	@Override
	public void undo()
	{
		Global.maker.getCurrentPane().getChildren().removeAll(imgNode.getBorder(), imgNode);
	}

	@Override
	public void redo()
	{
		execute();
	}

	@Override
	public Undoable copy()
	{
		return new AddNodeAction(this);
	}

	@Override
	public void execute()
	{
		Global.maker.getCurrentPane().getChildren().addAll(imgNode.getBorder(), imgNode);
	}

	@Override
	public boolean canExecute()
	{
		return imgNode != null;
	}

	@Override
	public void reset()
	{
		imgNode = null;
	}
}
