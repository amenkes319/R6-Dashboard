package dashboard.java.gestures;

import dashboard.java.actions.DrawAction;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class CanvasGestures
{
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
	
	private DrawAction drawAction = new DrawAction();
	private boolean bDraw = false;

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.PRIMARY) return;
			if (!Global.maker.isDrawSelected() && !Global.maker.isEraseSelected()) return;
			
			bDraw = true;
			double x = event.getX();
			double y = event.getY();
			drawAction.setOldX(x);
			drawAction.setOldY(y);
			drawAction.setX(x);
			drawAction.setY(y);
			drawAction.setOldCanvas(copyCanvas());
			drawAction.execute();
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (!event.isPrimaryButtonDown()) return;
			if (!bDraw) return;

			double x = event.getX();
			double y = event.getY();
			drawAction.setX(x);
			drawAction.setY(y);
			drawAction.execute();
			
			event.consume();
		}
	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>()
	{
		public void handle(MouseEvent event)
		{
			if (event.getButton() != MouseButton.PRIMARY) return;
			if (!Global.maker.isDrawSelected() && !Global.maker.isEraseSelected()) return;
			
			bDraw = false;
			drawAction.setNewCanvas(copyCanvas());
			UndoCollector.INSTANCE.add(drawAction);
			drawAction.reset();
			
			event.consume();
		}
	};
	
	private Canvas copyCanvas()
	{
		Canvas old = Global.maker.getCurrentPane().getCanvas();
		Canvas copyCanvas = new Canvas(old.getWidth(), old.getHeight());
		SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT); 
        WritableImage image = old.snapshot(params, null);
        copyCanvas.getGraphicsContext2D().drawImage(image, 0, 0);
        return copyCanvas;
	}
	
	public void setDrawAction(DrawAction drawAction)
	{
		this.drawAction = drawAction;
	}
}
