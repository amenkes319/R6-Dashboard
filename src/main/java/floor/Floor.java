package main.java.floor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import main.java.Global;
import main.java.actions.DrawAction;

public class Floor
{
	private ImageView floorView;
	private Canvas canvas;
	private GraphicsContext gc;
	private String name;
	
	private DrawAction drawAction;
	
	private boolean bDraw;
	
	public Floor(Image floor, String name)
	{
		this.floorView = new ImageView(floor);
		this.canvas = new Canvas(floorView.getFitWidth(), floorView.getFitHeight());
		this.gc = canvas.getGraphicsContext2D();
		this.name = name;
		
		this.drawAction = new DrawAction(gc);

		this.bDraw = false;
	}
	
	public void drawInit()
	{
		gc.setStroke(Color.BLACK);
		gc.setLineCap(StrokeLineCap.ROUND);
	    gc.setLineJoin(StrokeLineJoin.ROUND);
		gc.setLineWidth(Global.makerController.getLineWidthSlider().getValue());
		
		canvas.setOnMousePressed(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				bDraw = true;
				drawAction.setLastX(e.getX());
				drawAction.setLastY(e.getY());
			}
		});
		
		canvas.setOnMouseDragged(e ->
		{
			if (bDraw && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				drawAction.setX(e.getX());
				drawAction.setY(e.getY());
				drawAction.execute();
			}
		});

		canvas.setOnMouseReleased(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				bDraw = false;
				gc.save();
				gc.closePath();

//				UndoCollector.INSTANCE.add(drawAction);
				drawAction.reset();
			}
		});
	}
	
	public ImageView getFloorView()
	{
		return this.floorView;
	}
	
	public Canvas getCanvas()
	{
		return this.canvas;
	}
	
	public void setCanvas(Canvas canvas)
	{
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
	}
	
	public GraphicsContext getGC()
	{
		return this.gc;
	}
	
	public String toString()
	{
		return this.name;
	}
}
