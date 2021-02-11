package dashboard.java.zoompane;

import dashboard.java.actions.DrawAction;
import dashboard.java.gestures.CanvasGestures;
import dashboard.java.global.Global;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class ZoomPane extends Pane
{
	private DoubleProperty scale;
	
	private CanvasGestures canvasGestures;
	private Canvas canvas;
	private GraphicsContext gc;
	
	public ZoomPane(ImageView background)
	{
		canvas = new Canvas(background.getImage().getWidth(), background.getImage().getHeight());
		gc = canvas.getGraphicsContext2D();
		init();
		
		getChildren().addAll(background, canvas);
	}
	
	public ZoomPane(ImageView background, Canvas canvas)
	{
		
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();
		init();
		
		getChildren().addAll(background, this.canvas);
	}
	
	private void init()
	{
		canvasGestures = new CanvasGestures();
		scale = new SimpleDoubleProperty(1.0);

		scaleXProperty().bind(scale);
		scaleYProperty().bind(scale);
		drawInit();
	}

	public double getScale()
	{
		return scale.get();
	}

	public void setScale(double scale)
	{
		this.scale.set(scale);
	}

	public void setPivot(double x, double y)
	{
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}
	
	public void drawInit()
	{
		gc.setStroke(Global.maker.getColorPicker().getValue());
		gc.setLineCap(StrokeLineCap.ROUND);
	    gc.setLineJoin(StrokeLineJoin.ROUND);
		gc.setLineWidth(Global.maker.getLineWidthSlider().getValue());

		canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, canvasGestures.getOnMousePressedEventHandler());
		canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, canvasGestures.getOnMouseDraggedEventHandler());
		canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, canvasGestures.getOnMouseReleasedEventHandler());
	}
	
	public Canvas getCanvas()
	{
		return this.canvas;
	}
	
	public void setCanvas(Canvas canvas)
	{
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		
		if (getChildren().size() == 1)
			getChildren().add(canvas);
		else
			getChildren().set(1, canvas);
		
		canvasGestures.setDrawAction(new DrawAction());
		drawInit();
	}
	
	public GraphicsContext getGC()
	{
		return this.gc;
	}
}