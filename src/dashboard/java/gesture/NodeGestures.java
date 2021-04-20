package dashboard.java.gesture;

import dashboard.java.action.DeleteNodeAction;
import dashboard.java.action.MoveNodeAction;
import dashboard.java.action.RotateNodeAction;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class NodeGestures
{
	private DragContext nodeDragContext;
	private MoveNodeAction moveNodeAction;
	private RotateNodeAction rotateNodeAction;
	private DeleteNodeAction deleteNodeAction;
	
	public NodeGestures()
	{
		nodeDragContext = new DragContext();
		moveNodeAction = new MoveNodeAction();
		rotateNodeAction = new RotateNodeAction();
		deleteNodeAction = new DeleteNodeAction();
	}
	
	public EventHandler<MouseEvent> getOnMousePressedEventHandler()
	{
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler()
	{
		return onMouseDraggedEventHandler;
	}
	
	public EventHandler<MouseEvent> getOnMouseReleasedEventHandler()
	{
		return onMouseReleasedEventHandler;
	}
	
	public EventHandler<MouseEvent> getOnMouseClickedEventHandler()
	{
		return onMouseClickedEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.PRIMARY) return;
			if (Global.maker.isDeleteSelected()) return;

			ImageView node = (ImageView) event.getSource();
			nodeDragContext.setMouseAnchorX(event.getSceneX());
			nodeDragContext.setMouseAnchorY(event.getSceneY());
			nodeDragContext.setTranslateAnchorX(node.getTranslateX());
			nodeDragContext.setTranslateAnchorY(node.getTranslateY());
			
			if (Global.maker.isDragSelected())
			{
				moveNodeAction.setOldDragContext(nodeDragContext.copy());
				moveNodeAction.setNode((ImageView) node);
				moveNodeAction.setMouseEvent(event);
				moveNodeAction.setScale(Global.maker.getCurrentPane().getScale());
			}
			else if (Global.maker.isRotateSelected())
			{
				rotateNodeAction.setNode(node);
				rotateNodeAction.setOldTheta(node.getRotate());
			}
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (!event.isPrimaryButtonDown())
				return;

			if (Global.maker.isDragSelected())
			{
				moveNodeAction.setScale(Global.maker.getCurrentPane().getScale());
				moveNodeAction.setNode((ImageView) event.getSource());
				moveNodeAction.setMouseEvent(event);
				moveNodeAction.execute();
			}
			else if (Global.maker.isRotateSelected())
			{
				double deltaX = nodeDragContext.getMouseAnchorX() - event.getSceneX();
				double deltaY = nodeDragContext.getMouseAnchorY() - event.getSceneY();
				double theta = Math.atan(deltaY/deltaX) + Math.PI / 2;
				theta *= 180 / Math.PI;
				
				if (deltaX == 0 && deltaY == 0)
				{
					theta = 0;
				}
				else if (deltaX >= 0)
				{
					theta += 180;
				}
				
				if (!event.isControlDown())
				{
					int k = (int) theta / 45;
					if (theta % 45 >= 23)
						k++;
					
					theta = 45*k;
				}
				
				rotateNodeAction.setNewTheta(theta);
				rotateNodeAction.execute();
			}

			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.PRIMARY) return;

			ImageView node = (ImageView) event.getSource();
			if (Global.maker.isDragSelected())
			{
				nodeDragContext.setMouseAnchorX(event.getSceneX());
				nodeDragContext.setMouseAnchorY(event.getSceneY());	
				nodeDragContext.setTranslateAnchorX(node.getTranslateX());
				nodeDragContext.setTranslateAnchorY(node.getTranslateY());
				
				moveNodeAction.setNewDragContext(nodeDragContext.copy());
				UndoCollector.INSTANCE.add(moveNodeAction.copy());
				moveNodeAction.reset();
			}			
			else if (Global.maker.isRotateSelected())
			{
				UndoCollector.INSTANCE.add(rotateNodeAction);
				
				rotateNodeAction.reset();
			}
			
			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (Global.maker.isDeleteSelected())
			{
				ImageView node = (ImageView) event.getSource();
				deleteNodeAction.setNode(node);
				deleteNodeAction.execute();
	
				UndoCollector.INSTANCE.add(deleteNodeAction);
				deleteNodeAction.reset();
			}
		}
	};
}
