package dashboard.java.gestures;

import dashboard.java.actions.DeleteNodeAction;
import dashboard.java.actions.MoveNodeAction;
import dashboard.java.actions.RotateNodeAction;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class NodeGestures
{
	private DragContext nodeDragContext = new DragContext();
	private MoveNodeAction moveNodeAction = new MoveNodeAction();
	private RotateNodeAction rotateNodeAction = new RotateNodeAction();
	private DeleteNodeAction deleteNodeAction = new DeleteNodeAction();
	
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
			if (event.getButton() != MouseButton.PRIMARY)
				return;

			nodeDragContext.setMouseAnchorX(event.getSceneX());
			nodeDragContext.setMouseAnchorY(event.getSceneY());

			Node node = (Node) event.getSource();

			nodeDragContext.setTranslateAnchorX(node.getTranslateX());
			nodeDragContext.setTranslateAnchorY(node.getTranslateY());
			
			moveNodeAction.setOldDragContext(nodeDragContext.copy());
			moveNodeAction.setNode(node);
			moveNodeAction.setMouseEvent(event);
			moveNodeAction.setScale(Global.maker.getCurrentPane().getScale());
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (!event.isPrimaryButtonDown())
				return;

			moveNodeAction.setScale(Global.maker.getCurrentPane().getScale());
			moveNodeAction.setNode((Node) event.getSource());
			moveNodeAction.setMouseEvent(event);
			moveNodeAction.execute();

			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.PRIMARY)
				return;

			nodeDragContext.setMouseAnchorX(event.getSceneX());
			nodeDragContext.setMouseAnchorY(event.getSceneY());

			Node node = (Node) event.getSource();

			nodeDragContext.setTranslateAnchorX(node.getTranslateX());
			nodeDragContext.setTranslateAnchorY(node.getTranslateY());
			
			moveNodeAction.setNewDragContext(nodeDragContext.copy());
			UndoCollector.INSTANCE.add(moveNodeAction.copy());
			moveNodeAction.reset();
			
			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (!Global.maker.isDeleteSelected()) return;
			
			Node node = (Node) event.getSource();
			deleteNodeAction.setNode(node);
			deleteNodeAction.execute();
			UndoCollector.INSTANCE.add(deleteNodeAction);
			deleteNodeAction.reset();
		}
	};
	
	private void updateNodeDragContext()
	{
		
	}
}
