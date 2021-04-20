package dashboard.java.action;

import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.image.ImageView;

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
		Global.maker.getCurrentPane().getChildren().removeAll(Global.maker.getBorder(imgView), imgView);
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
		Global.maker.getCurrentPane().getChildren().addAll(Global.maker.getBorder(imgView), imgView);
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