package dashboard.java.actions;

import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DrawAction implements Action, Undoable
{
	private static final double ERASER_MULTIPLIER = 1.4;

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
		Global.maker.getCurrentPane().setCanvas(oldCanvas);

	}

	@Override
	public void redo()
	{
		Global.maker.getCurrentPane().setCanvas(newCanvas);
	}

	@Override
	public Undoable copy()
	{
		return new DrawAction(this);
	}

	@Override
	public void execute()
	{
		GraphicsContext gc = Global.maker.getCurrentPane().getGC();
		if (Global.maker.isEraseSelected())
		{
			gc.clearRect(x - (gc.getLineWidth() * ERASER_MULTIPLIER / 2), y - (gc.getLineWidth() * ERASER_MULTIPLIER / 2), gc.getLineWidth() * ERASER_MULTIPLIER, gc.getLineWidth() * ERASER_MULTIPLIER);
		} 
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
