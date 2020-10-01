package main.java.floor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import main.java.Global;
import main.java.actions.DrawAction;

public class Floor
{
	private ImageView floorView;
	private CloneableCanvas canvas;
	private GraphicsContext gc;
	private String name;
	
	private DrawAction drawAction;
	
	private boolean bDraw;
	
	public Floor(Image floor, String name)
	{
		this.floorView = new ImageView(floor);
		this.canvas = new CloneableCanvas(floorView.getFitWidth(), floorView.getFitHeight());
		this.gc = canvas.getGraphicsContext2D();
		this.name = name;
		
		this.drawAction = new DrawAction();

		this.bDraw = false;
	}
	
	public void drawInit()
	{
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(Global.makerController.getLineWidthSlider().getValue());
		
		canvas.setOnMousePressed(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				try
				{
					drawAction.setOldCanvas(canvas.clone());
				} 
				catch (CloneNotSupportedException e1)
				{
					e1.printStackTrace();
				}
				
				bDraw = true;
				gc.beginPath();
				drawAction.setX(e.getX());
				drawAction.setY(e.getY());
				drawAction.setNewCanvas(canvas);
				drawAction.execute();
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
	
	public void setCanvas(CloneableCanvas canvas)
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
