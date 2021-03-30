package dashboard.java.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.Gson;

import dashboard.java.Map;
import dashboard.java.actions.DrawAction;
import dashboard.java.global.Global;
import dashboard.java.zoompane.ZoomPane;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;

public class Data
{
	private String map;
	private LinkedList<Stroke> strokes;
	private LinkedList<NodeData> nodes;

	public Data()
	{
		map = null;
		strokes = new LinkedList<>();
		nodes = new LinkedList<>();
	}
	
	public void setMap(Map map)
	{
		this.map = map.toString();
	}

	public Map getMap()
	{
		return Map.valueOf(map.toUpperCase());
	}
	
	public void addStroke(Stroke stroke)
	{
		strokes.add(stroke);
	}

	public LinkedList<Stroke> getStrokes()
	{
		return strokes;
	}

	public void addNode(Node node, String path, String tab)
	{
		nodes.add(new NodeData(path, node.getId(), node.getTranslateX(), node.getTranslateY(), node.getRotate(), tab));
	}
	
	public LinkedList<NodeData> getNodes()
	{
		return nodes;
	}
	
	private void fillData()
	{
		
		for (int i = 0; i < nodes.size(); i++)
		{
			NodeData nodeData = nodes.get(i);
			boolean bFound = false;

			Tab currentTab = null;
			for (Tab tab : Global.maker.getTabPane().getTabs())
			{
				if (tab.getText().equals(nodeData.getTab()))
				{
					currentTab = tab;
					break;
				}
			}
			
			ObservableList<Node> list = ((ZoomPane) currentTab.getContent()).getChildren();
			for (Node node : list)
			{
				if (nodeData.getId().equals(node.getId()))
				{
					nodeData.setX(node.getTranslateX());
					nodeData.setY(node.getTranslateY());
					nodeData.setAngle(node.getRotate());
					
					bFound = true;
				}
			}
			
			if (!bFound)
				nodes.remove(nodes.get(i--));
		}
	}

	public static void serialize(File file)
	{
		Global.data.fillData();
		Gson gson = new Gson();
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(gson.toJson(Global.data));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deserialize(File file)
	{
		String json = "";
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Iterator<String> it = reader.lines().iterator();
			while (it.hasNext())
			{
				json += it.next();
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		Data data = gson.fromJson(json, Data.class);
		
		Global.maker.show(data.getMap());
		
		for (Stroke stroke : data.getStrokes())
		{
			Tab currentTab = null;
			for (Tab tab : Global.maker.getTabPane().getTabs())
			{
				if (tab.getText().equals(stroke.getTab()))
				{
					currentTab = tab;
					break;
				}
			}
			GraphicsContext gc = ((ZoomPane) currentTab.getContent()).getGC();
			gc.setLineWidth(stroke.getWidth());
			
			double width = gc.getLineWidth();
			if (stroke.isEraser())
			{
				gc.setFill(new Color(0, 0, 0, 1));
				gc.clearRect(stroke.getX2() - (width * DrawAction.ERASER_MULTIPLIER / 2),
							 stroke.getY2() - (width * DrawAction.ERASER_MULTIPLIER / 2),
							 width * DrawAction.ERASER_MULTIPLIER,
							 width * DrawAction.ERASER_MULTIPLIER);
			}
			else
			{
				gc.setStroke(new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), stroke.getOpacity()));
				gc.strokeLine(stroke.getX1(), stroke.getY1(), stroke.getX2(), stroke.getY2());
			}
			gc.stroke();
		}
		
		for (NodeData nodeData : data.getNodes())
		{
			Global.maker.addNodeJSON(nodeData);
		}
	}
}
