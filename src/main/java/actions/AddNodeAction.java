package main.java.actions;

import javafx.scene.image.ImageView;
import main.java.global.Global;
import main.java.undo.Undoable;

public class AddNodeAction implements Action, Undoable
{
	private ImageView imgView;
	
	public AddNodeAction()
	{
		reset();
	}
	
	public AddNodeAction(AddNodeAction addNodeAction)
	{
		this.imgView = addNodeAction.getImageView();
	}

	public AddNodeAction(ImageView imgView)
	{
		this.imgView = imgView;
	}

	public ImageView getImageView()
	{
		return imgView;
	}

	public void setImageView(ImageView imgView)
	{
		this.imgView = imgView;
	}

	@Override
	public void undo()
	{
		Global.makerController.getCurrentAnchorPane().getChildren().remove(imgView);
	}

	@Override
	public void redo()
	{
		execute();
	}

	@Override
	public Undoable copy()
	{
		return new AddNodeAction(this);
	}

	@Override
	public void execute()
	{
		Global.makerController.getCurrentAnchorPane().getChildren().add(imgView);
	}

	@Override
	public boolean canExecute()
	{
		return imgView != null;
	}

	@Override
	public void reset()
	{
		imgView = null;
	}
}
