package stratmaker.java.actions;

import javafx.scene.image.ImageView;
import stratmaker.java.undo.Undoable;

public class RotateNodeAction implements Action, Undoable
{
	private ImageView imgView;
	private double oldTheta, newTheta;

	public RotateNodeAction()
	{
		reset();
	}
	
	public RotateNodeAction(ImageView imgView)
	{
		this.imgView = imgView;
		this.oldTheta = 0;
		this.newTheta = 0;
	}
	
	public RotateNodeAction(RotateNodeAction other)
	{
		this.imgView = other.getImageView();
		this.oldTheta = other.getOldTheta();
		this.newTheta = other.getNewTheta();
	}
	
	public ImageView getImageView()
	{
		return imgView;
	}
	
	public void setImageView(ImageView imgView)
	{
		this.imgView = imgView;
	}
	
	public double getOldTheta()
	{
		return oldTheta;
	}

	public void setOldTheta(double oldTheta)
	{
		this.oldTheta = oldTheta;
	}

	public double getNewTheta()
	{
		return newTheta;
	}

	public void setNewTheta(double newTheta)
	{
		this.newTheta = newTheta;
	}

	@Override
	public void undo()
	{
		imgView.setRotate(oldTheta);
	}

	@Override
	public void redo()
	{
		System.out.println(newTheta);
		imgView.setRotate(newTheta);
	}

	@Override
	public Undoable copy()
	{
		return new RotateNodeAction(this);
	}

	@Override
	public void execute()
	{
		imgView.setRotate(newTheta);
	}

	@Override
	public boolean canExecute()
	{
		return true;
	}

	@Override
	public void reset()
	{
		this.imgView = null;
		this.oldTheta = 0;
		this.newTheta = 0;
	}

}
