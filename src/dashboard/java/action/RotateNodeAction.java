package dashboard.java.action;

import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.image.ImageView;

public class RotateNodeAction implements Action, Undoable
{
	private ImageView node;
	private double oldTheta, newTheta;

	public RotateNodeAction()
	{
		reset();
	}
	
	public RotateNodeAction(ImageView node)
	{
		this.node = node;
		this.oldTheta = 0;
		this.newTheta = 0;
	}
	
	public RotateNodeAction(RotateNodeAction other)
	{
		this.node = other.getNode();
		this.oldTheta = other.getOldTheta();
		this.newTheta = other.getNewTheta();
	}
	
	public ImageView getNode()
	{
		return node;
	}
	
	public void setNode(ImageView node)
	{
		this.node = node;
	}
	
	public double getOldTheta()
	{
		return oldTheta;
	}

	public void setOldTheta(double oldTheta)
	{
		this.oldTheta = oldTheta;
	}

	public double getNewTheta()
	{
		return newTheta;
	}

	public void setNewTheta(double newTheta)
	{
		this.newTheta = newTheta;
	}

	@Override
	public void undo()
	{
		node.setRotate(oldTheta);
		Global.maker.getBorder(node).setRotate(oldTheta);
	}

	@Override
	public void redo()
	{
		node.setRotate(newTheta);
		Global.maker.getBorder(node).setRotate(newTheta);
	}

	@Override
	public Undoable copy()
	{
		return new RotateNodeAction(this);
	}

	@Override
	public void execute()
	{
		node.setRotate(newTheta);
		Global.maker.getBorder(node).setRotate(newTheta);
	}

	@Override
	public boolean canExecute()
	{
		return node != null;
	}

	@Override
	public void reset()
	{
		this.node = null;
		this.oldTheta = 0;
		this.newTheta = 0;
	}
}
