package dashboard.java.action;

import dashboard.java.gesture.DragContext;
import dashboard.java.undo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class MoveNodeAction implements Action, Undoable
{
	private Node node;
	private DragContext oldDragContext, newDragContext;
	private MouseEvent mouseEvent;
	private double scale;

	public MoveNodeAction()
	{
		reset();
	}
	
	public MoveNodeAction(MoveNodeAction moveNodeAction)
	{
		this.node = moveNodeAction.getNode();
		this.oldDragContext = moveNodeAction.getOldDragContext();
		this.newDragContext = moveNodeAction.getNewDragContext();
		this.mouseEvent = null;
		this.scale = moveNodeAction.getScale();
	}
	
	public MoveNodeAction(Node node, DragContext oldDragContext, DragContext newDragContext, MouseEvent mouseEvent, double scale)
	{
		this.node = node;
		this.oldDragContext = oldDragContext;
		this.newDragContext = newDragContext;
		this.mouseEvent = mouseEvent;
		this.scale = scale;
	}

	public Node getNode()
	{
		return node;
	}

	public void setNode(Node node)
	{
		this.node = node;
	}
	
	public DragContext getOldDragContext()
	{
		return oldDragContext;
	}
	
	public void setOldDragContext(DragContext oldDragContext)
	{
		this.oldDragContext = oldDragContext;
	}
	
	public DragContext getNewDragContext()
	{
		return newDragContext;
	}
	
	public void setNewDragContext(DragContext newDragContext)
	{
		this.newDragContext = newDragContext;
	}

	@Override
	public void undo()
	{
		double x = oldDragContext.getTranslateAnchorX() / scale;
		double y = oldDragContext.getTranslateAnchorY() / scale;
		node.setTranslateX(x);
		node.setTranslateY(y);
	}

	@Override
	public void redo()
	{
		double x = newDragContext.getTranslateAnchorX() / scale;
		double y = newDragContext.getTranslateAnchorY() / scale;
		node.setTranslateX(x);
		node.setTranslateY(y);
	}
	
	@Override
	public Undoable copy()
	{
		return new MoveNodeAction(this);
	}
	
	@Override
	public void execute()
	{
		double x = oldDragContext.getTranslateAnchorX() + ((mouseEvent.getSceneX() - oldDragContext.getMouseAnchorX()) / scale);
		double y = oldDragContext.getTranslateAnchorY() + ((mouseEvent.getSceneY() - oldDragContext.getMouseAnchorY()) / scale);
		node.setTranslateX(x);
		node.setTranslateY(y);
	}

	@Override
	public boolean canExecute()
	{
		return node != null;
	}
	
	public void reset()
	{
		this.node = null;
		this.oldDragContext = null;
		this.newDragContext = null;
		this.mouseEvent = null;
		this.scale = 0.0;
	}
	
	public void setMouseEvent(MouseEvent mouseEvent)
	{
		this.mouseEvent = mouseEvent;
	}
	
	public double getScale()
	{
		return this.scale;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
}
