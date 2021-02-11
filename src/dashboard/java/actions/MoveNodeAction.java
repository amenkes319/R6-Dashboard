package dashboard.java.actions;

import dashboard.java.gestures.DragContext;
import dashboard.java.undo.Undoable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
		this.scale = 1;
	}
	
	public MoveNodeAction(ImageView node, DragContext oldDragContext, DragContext newDragContext, MouseEvent mouseEvent, double scale)
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
	
	public void reset()
	{
		this.node = null;
		this.oldDragContext = null;
		this.newDragContext = null;
		this.mouseEvent = null;
		this.scale = 0.0;
	}

	@Override
	public void execute()
	{
		node.setTranslateX(oldDragContext.getTranslateAnchorX() + ((mouseEvent.getSceneX() - oldDragContext.getMouseAnchorX()) / scale));
		node.setTranslateY(oldDragContext.getTranslateAnchorY() + ((mouseEvent.getSceneY() - oldDragContext.getMouseAnchorY()) / scale));
	}

	@Override
	public boolean canExecute()
	{
		return node != null;
	}

	@Override
	public void undo()
	{
		node.setTranslateX(oldDragContext.getTranslateAnchorX() / scale);
		node.setTranslateY(oldDragContext.getTranslateAnchorY() / scale);
	}

	@Override
	public void redo()
	{
		node.setTranslateX(newDragContext.getTranslateAnchorX() / scale);
		node.setTranslateY(newDragContext.getTranslateAnchorY() / scale);
	}
	
	@Override
	public Undoable copy()
	{
		return new MoveNodeAction(this);
	}
	
	public void setMouseEvent(MouseEvent mouseEvent)
	{
		this.mouseEvent = mouseEvent;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
}
