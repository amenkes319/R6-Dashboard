package dashboard.java.model;

import java.util.LinkedList;

public class Stroke implements java.io.Serializable
{
	private static final long serialVersionUID = -1361542108137493820L;
	private LinkedList<Line> lines;
	
	public Stroke()
	{
		lines = new LinkedList<>();
	}
	
	public void addLine(Line line)
	{
		lines.add(line);
	}
	
	public LinkedList<Line> getLines()
	{
		return lines;
	}
}
