package stratmaker.java.actions;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import stratmaker.java.global.Global;
import stratmaker.java.undo.Undoable;

public class DrawAction implements Action, Undoable
{
	private Canvas oldCanvas, newCanvas;
	private double x, y, oldX, oldY;

	public DrawAction()
	{
		reset();
	}

	public DrawAction(DrawAction drawAction)
	{
		this.oldCanvas = drawAction.getOldCanvas();
		this.newCanvas = drawAction.getNewCanvas();
		this.x = drawAction.getX();
		this.y = drawAction.getY();
		this.oldX = drawAction.getOldX();
		this.oldY = drawAction.getOldY();
	}

	@Override
	public void undo()
	{
		System.out.println(Global.makerController.getCurrentAnchorPane().getChildren().get(1));
		Global.makerController.getCurrentAnchorPane().getChildren().set(1, oldCanvas);
		Global.makerController.getCurrentFloor().setCanvas(oldCanvas);
	}

	@Override
	public void redo()
	{
		Global.makerController.getCurrentAnchorPane().getChildren().set(1, newCanvas);
		Global.makerController.getCurrentFloor().setCanvas(newCanvas);
	}

	@Override
	public Undoable copy()
	{
		return new DrawAction(this);
	}

	@Override
	public void execute()
	{
		GraphicsContext gc = Global.makerController.getCurrentFloor().getGC();
		if (Global.makerController.eraseSelected())
			gc.clearRect(x - (gc.getLineWidth() / 2), y - (gc.getLineWidth() / 2), gc.getLineWidth(), gc.getLineWidth());
		else
		{
			gc.strokeLine(oldX, oldY, x, y);
			
			oldX = x;
			oldY = y;
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
		this.oldCanvas = null;
		this.newCanvas = null;
		this.x = 0;
		this.y = 0;
		this.oldX = 0;
		this.oldY = 0;
	}
	
	public Canvas getOldCanvas()
	{
		return oldCanvas;
	}

	public void setOldCanvas(Canvas oldCanvas)
	{
		this.oldCanvas = oldCanvas;
	}

	public Canvas getNewCanvas()
	{
		return newCanvas;
	}

	public void setNewCanvas(Canvas newCanvas)
	{
		this.newCanvas = newCanvas;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getOldX()
	{
		return oldX;
	}

	public void setOldX(double oldX)
	{
		this.oldX = oldX;
	}

	public double getOldY()
	{
		return oldY;
	}

	public void setOldY(double oldY)
	{
		this.oldY = oldY;
	}
}
