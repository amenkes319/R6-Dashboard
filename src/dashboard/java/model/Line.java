package dashboard.java.model;

import javafx.scene.paint.Paint;

public class Line implements java.io.Serializable
{
	private static final long serialVersionUID = 7136434112612385774L;
	
	private double x1, y1, x2, y2, width;
	private Paint color;
	
	public Line(double x1, double y1, double x2, double y2, double width, Paint color)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.color = color;
	}

	/*
	 * for eraser marks
	 */
	public Line(double x1, double y1, double x2, double y2, double width)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.color = null;
	}

	public double getX1()
	{
		return x1;
	}

	public double getY1()
	{
		return y1;
	}

	public double getX2()
	{
		return x2;
	}

	public double getY2()
	{
		return y2;
	}

	public double getWidth()
	{
		return width;
	}

	public Paint getColor()
	{
		return color;
	}
	
	public boolean isEraser()
	{
		return color == null;
	}
	
	public String toString()
	{
		return "(" + x1 + "," + y1 + "," + y2 + "," + x2 + "," + width + "," + color + ")";
	}
}
