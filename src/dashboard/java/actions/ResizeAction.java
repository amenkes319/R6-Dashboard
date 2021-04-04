package dashboard.java.actions;

import dashboard.java.undo.Undoable;
import javafx.scene.image.ImageView;

public class ResizeAction implements Action, Undoable
{
	private ImageView node;
	private double oldScale, newScale;

	public ResizeAction()
	{
		reset();
	}
	
	public ResizeAction(ResizeAction resizeAction)
	{
		node = resizeAction.getNode();
		oldScale = resizeAction.getOldScale();
		newScale = resizeAction.getNewScale();
	}

	@Override
	public void undo()
	{
		System.out.println(node);
		node.setScaleX(oldScale);
		node.setScaleY(oldScale);
	}

	@Override
	public void redo()
	{
		node.setScaleX(newScale);
		node.setScaleY(newScale);
	}

	@Override
	public Undoable copy()
	{
		return new ResizeAction(this);
	}

	@Override
	public void execute()
	{
		node.setScaleX(newScale);
		node.setScaleY(newScale);
	}

	@Override
	public boolean canExecute()
	{
		return false;
	}

	@Override
	public void reset()
	{
		node = null;
		oldScale = 1;
		newScale = 1;
	}

	public ImageView getNode()
	{
		return node;
	}

	public void setNode(ImageView node)
	{
		this.node = node;
	}

	public double getOldScale()
	{
		return oldScale;
	}

	public void setOldScale(double oldScale)
	{
		this.oldScale = oldScale;
	}

	public double getNewScale()
	{
		return newScale;
	}

	public void setNewScale(double newScale)
	{
		this.newScale = newScale;
	}
}
