package main.java.actions;

import javafx.scene.image.ImageView;
import main.java.undo.Undoable;

public class MoveNodeAction implements Action, Undoable
{
	ImageView imgView;
	
	double oldX, oldY, newX, newY;

	public MoveNodeAction()
	{
		reset();
	}
	
	public MoveNodeAction(MoveNodeAction moveNodeAction)
	{
		this.imgView = moveNodeAction.getImageView();
		this.oldX = moveNodeAction.getOldX();
		this.oldY = moveNodeAction.getOldY();
		this.newX = moveNodeAction.getNewX();
		this.newY = moveNodeAction.getNewY();
	}
	
	public MoveNodeAction(ImageView img, double oldX, double oldY, double newX, double newY)
	{
		this.imgView = img;
		this.oldX = oldX;
		this.oldY = oldY;
		this.newX = newX;
		this.newY = newY;
	}

	public ImageView getImageView()
	{
		return imgView;
	}

	public void setImageView(ImageView img)
	{
		this.imgView = img;
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

	public double getNewX()
	{
		return newX;
	}

	public void setNewX(double newX)
	{
		this.newX = newX;
	}

	public double getNewY()
	{
		return newY;
	}

	public void setNewY(double newY)
	{
		this.newY = newY;
	}
	
	public void reset()
	{
		this.imgView = null;
		this.oldX = 0;
		this.oldY = 0;
		this.newX = 0;
		this.newY = 0;
	}

	@Override
	public void execute()
	{
		imgView.setX(newX);
		imgView.setY(newY);
	}

	@Override
	public boolean canExecute()
	{
		return imgView != null;
	}

	@Override
	public void undo()
	{
		imgView.setX(oldX);
		imgView.setY(oldY);
	}

	@Override
	public void redo()
	{
		imgView.setX(newX);
		imgView.setY(newY);
	}
	
	@Override
	public Undoable copy()
	{
		return new MoveNodeAction(this);
	}
}
