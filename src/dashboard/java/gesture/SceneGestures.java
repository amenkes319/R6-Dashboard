package dashboard.java.gesture;

import dashboard.java.ZoomPane;
import dashboard.java.global.Global;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class SceneGestures
{
	private static final double MAX_SCALE = 4.0d;
	private static final double MIN_SCALE = .19d;

	private DragContext sceneDragContext;
	
	public SceneGestures()
	{
		sceneDragContext = new DragContext();
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
		return onClickEventHandler;
	}
	
	public EventHandler<ScrollEvent> getOnScrollEventHandler()
	{
		return onScrollEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.MIDDLE) return;
			
			Global.primaryStage.getScene().setCursor(Cursor.MOVE);
			sceneDragContext.setMouseAnchorX(event.getSceneX());
			sceneDragContext.setMouseAnchorY(event.getSceneY());

			ZoomPane pane = Global.maker.getCurrentPane();
			
			sceneDragContext.setTranslateAnchorX(pane.getTranslateX());
			sceneDragContext.setTranslateAnchorY(pane.getTranslateY());
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (!event.isMiddleButtonDown()) return;
			
			ZoomPane pane = Global.maker.getCurrentPane();

			pane.setTranslateX(sceneDragContext.getTranslateAnchorX() + event.getSceneX() - sceneDragContext.getMouseAnchorX());
			pane.setTranslateY(sceneDragContext.getTranslateAnchorY() + event.getSceneY() - sceneDragContext.getMouseAnchorY());

			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.MIDDLE)
				return;

			Global.primaryStage.getScene().setCursor(Cursor.DEFAULT);
		}
	};
	
	private EventHandler<MouseEvent> onClickEventHandler = new EventHandler<MouseEvent>()
	{
		@Override
		public void handle(MouseEvent event)
		{
			if (Global.maker.isDragSelected())
			{
				if (event.getTarget() instanceof ImageView)
					Global.maker.setSelectedNode((ImageView) event.getTarget());
				else
					Global.maker.setSelectedNode(null);
			}
		}
	};

	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>()
	{
		@Override
		public void handle(ScrollEvent event)
		{
			ZoomPane pane = Global.maker.getCurrentPane();

			double delta = 1.2;
			double scale = pane.getScale();
			double oldScale = scale;

			if (event.getDeltaY() < 0)
				scale /= delta;
			else
				scale *= delta;

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;

			double dx = (event.getSceneX() - (pane.getBoundsInParent().getWidth() / 2 + pane.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY() - (pane.getBoundsInParent().getHeight() / 2 + pane.getBoundsInParent().getMinY()));

			pane.setScale(scale);
			pane.setPivot(f * dx, f * dy);

			event.consume();
		}
	};

	private double clamp(double value, double min, double max)
	{
		if (Double.compare(value, min) < 0) return min;
		if (Double.compare(value, max) > 0) return max;

		return value;
	}
}