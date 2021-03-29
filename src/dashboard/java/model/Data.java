package dashboard.java.model;

import java.util.LinkedList;

public class Data implements java.io.Serializable
{
	private static final long serialVersionUID = -6178476704237311309L;
	private static LinkedList<Stroke> strokes = new LinkedList<>();
	
	public static void addStroke()
	{
		strokes.add(new Stroke());
	}
	
	public static LinkedList<Stroke> getStrokes()
	{
		return strokes;
	}
}
