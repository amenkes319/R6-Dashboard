package main.java.actions;

import javafx.scene.canvas.GraphicsContext;
import main.java.Global;
import main.java.floor.CloneableCanvas;
import main.java.undo.Undoable;

public class DrawAction implements Action, Undoable
{
	private CloneableCanvas oldCanvas, newCanvas;
	private GraphicsContext newGC;
	private double x, y;

	public DrawAction()
	{
		reset();
	}

	public DrawAction(CloneableCanvas oldCanvas, CloneableCanvas newCanvas)
	{
		this.oldCanvas = oldCanvas;
		this.newCanvas = newCanvas;
		this.newGC = newCanvas.getGraphicsContext2D();
		this.x = 0;
		this.y = 0;
	}

	public DrawAction(DrawAction drawAction)
	{
		this.oldCanvas = drawAction.getOldCanvas();
		this.newCanvas = drawAction.getNewCanvas();
		this.newGC = newCanvas.getGraphicsContext2D();
		this.x = 0;
		this.y = 0;
	}

	public CloneableCanvas getOldCanvas()
	{
		return oldCanvas;
	}
	
	public void setOldCanvas(CloneableCanvas oldCanvas)
	{
		this.oldCanvas = oldCanvas;
	}

	public CloneableCanvas getNewCanvas()
	{
		return newCanvas;
	}
	
	public void setNewCanvas(CloneableCanvas newCanvas)
	{
		this.newCanvas = newCanvas;
		this.newGC = newCanvas.getGraphicsContext2D();
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	@Override
	public void undo()
	{
		Global.makerController.updateFloorCanvas(oldCanvas);
	}

	@Override
	public void redo()
	{
		Global.makerController.updateFloorCanvas(newCanvas);
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
			newGC.clearRect(x - (newGC.getLineWidth() / 2), y - (newGC.getLineWidth() / 2), newGC.getLineWidth(), newGC.getLineWidth());
		else
			newGC.lineTo(x, y);
		newGC.stroke();
	}

	@Override
	public boolean canExecute()
	{
		return true;
	}

	@Override
	public void reset()
	{
		oldCanvas = null;
		newCanvas = null;
		this.newGC = null;
		x = 0;
		y = 0;
	}
}
