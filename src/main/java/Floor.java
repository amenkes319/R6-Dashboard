package main.java;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class Floor
{
	private ImageView floorView;
	private Canvas canvas;
	private GraphicsContext gc;
	private String name;
	private boolean bDraw;
	
	public Floor(Image floor, String name, int width, int height)
	{
		this.name = name;
		this.floorView = new ImageView(floor);
		this.canvas = new Canvas(floorView.getFitWidth(), floorView.getFitHeight());
		this.gc = canvas.getGraphicsContext2D();
	}
	
	public void initDraw()
	{
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(4);
		
		canvas.setOnMousePressed(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && Global.makerController.drawSelected())
			{
				bDraw = true;
				gc.beginPath();
				gc.lineTo(e.getX(), e.getY());
				gc.stroke();
			}
		});
		
		canvas.setOnMouseDragged(e ->
		{
			if (bDraw && Global.makerController.drawSelected())
			{
				gc.lineTo(e.getX(), e.getY());
				gc.stroke();
			}
		});

		canvas.setOnMouseReleased(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && Global.makerController.drawSelected())
			{
				bDraw = false;
				gc.save();
				gc.closePath();
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
	
	public GraphicsContext getGC()
	{
		return this.gc;
	}
	
	public String toString()
	{
		return this.name;
	}
}
