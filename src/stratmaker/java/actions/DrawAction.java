package stratmaker.java.actions;

import javafx.scene.canvas.GraphicsContext;
import stratmaker.java.global.Global;
import stratmaker.java.undo.Undoable;

public class DrawAction implements Action, Undoable
{
	private GraphicsContext gc;
	private double x, y, lastX, lastY;

	public DrawAction(GraphicsContext gc)
	{
		this.gc = gc;
		this.x = 0;
		this.y = 0;
		this.lastX = 0;
		this.lastY = 0;
	}

	public DrawAction(DrawAction drawAction)
	{
		this.gc = drawAction.getGC();
		this.x = 0;
		this.y = 0;
		this.lastX = 0;
		this.lastY = 0;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public void setLastX(double lastX)
	{
		this.lastX = lastX;
	}

	public void setLastY(double lastY)
	{
		this.lastY = lastY;
	}

	public GraphicsContext getGC()
	{
		return this.gc;
	}
	
	@Override
	public void undo()
	{
		
	}

	@Override
	public void redo()
	{
		
	}

	@Override
	public Undoable copy()
	{
		return new DrawAction(this);
	}

	@Override
	public void execute()
	{
		if (Global.makerController.eraseSelected())
			gc.clearRect(x - (gc.getLineWidth() / 2), y - (gc.getLineWidth() / 2), gc.getLineWidth(), gc.getLineWidth());
		else
		{
			gc.strokeLine(lastX, lastY, x, y);
			
			lastX = x;
			lastY = y;
		}
		
		gc.stroke();
	}

	@Override
	public boolean canExecute()
	{
		return true;
	}

	@Override
	public void reset()
	{
		x = 0;
		y = 0;
		lastX = 0;
		lastY = 0;
	}
}
