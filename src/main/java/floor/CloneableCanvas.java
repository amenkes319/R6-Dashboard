package main.java.floor;

import javafx.scene.canvas.Canvas;

public class CloneableCanvas extends Canvas implements Cloneable
{

	public CloneableCanvas(double width, double height)
	{
		super(width, height);
	}
	
	public CloneableCanvas clone() throws CloneNotSupportedException
	{
		return (CloneableCanvas) super.clone();
	}
}
