package dashboard.java.actions;

import dashboard.java.global.Global;
import dashboard.java.model.Stroke;
import dashboard.java.undo.Undoable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DrawAction implements Action, Undoable
{
	public static final double ERASER_MULTIPLIER = 1.4;

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
			double width = gc.getLineWidth();
			gc.clearRect(x - (width * ERASER_MULTIPLIER / 2), y - (width * ERASER_MULTIPLIER / 2), width * ERASER_MULTIPLIER, width * ERASER_MULTIPLIER);
			Global.data.addStroke(new Stroke(Global.maker.getCurrentTab().getText(), oldX, oldY, x, y, gc.getLineWidth()));
		}
		else
		{
			gc.strokeLine(oldX, oldY, x, y);
			Global.data.addStroke(new Stroke(Global.maker.getCurrentTab().getText(), oldX, oldY, x, y, gc.getLineWidth(), (Color) gc.getStroke()));
			
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
		oldCanvas = null;
		newCanvas = null;
		x = 0;
		y = 0;
		oldX = 0;
		oldY = 0;
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
