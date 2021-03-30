package dashboard.java.model;

import javafx.scene.paint.Color;

public class Stroke
{
	private double x1, y1, x2, y2, width;
	private double r, g, b, a;
	private String tab;
	
	public Stroke(String tab, double x1, double y1, double x2, double y2, double width, Color color)
	{
		this.tab = tab;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.r = color.getRed();
		this.g = color.getGreen();
		this.b = color.getBlue();
		this.a = color.getOpacity();
	}

	/*
	 * for eraser marks
	 */
	public Stroke(String tab, double x1, double y1, double x2, double y2, double width)
	{
		this.tab = tab;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.r = -1;
		this.g = -1;
		this.b = -1;
		this.a = -1;
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

	public double getRed()
	{
		return r;
	}

	public double getGreen()
	{
		return g;
	}

	public double getBlue()
	{
		return b;
	}

	public double getOpacity()
	{
		return a;
	}
	
	public boolean isEraser()
	{
		return r == -1;
	}
	
	public String getTab()
	{
		return tab;
	}

	public void setTab(String tab)
	{
		this.tab = tab;
	}

	public String toString()
	{
		return "(" + tab + "," + x1 + ", " + y1 + ", " + y2 + ", " + x2 + ", " + width + ", " + r + ", " + g + ", " + b + ", " + a + ")";
	}
}
