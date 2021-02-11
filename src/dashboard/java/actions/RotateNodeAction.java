package dashboard.java.actions;

import dashboard.java.undo.Undoable;
import javafx.scene.Node;

public class RotateNodeAction implements Action, Undoable
{
	private Node node;
	private double oldTheta, newTheta;

	public RotateNodeAction()
	{
		reset();
	}
	
	public RotateNodeAction(Node node)
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
	
	public Node getNode()
	{
		return node;
	}
	
	public void setNode(Node node)
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
	}

	@Override
	public void redo()
	{
		System.out.println(newTheta);
		node.setRotate(newTheta);
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
		this.oldTheta = 0;
		this.newTheta = 0;
	}
}
