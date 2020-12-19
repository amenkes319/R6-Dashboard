package stratmaker.java.actions;

import javafx.scene.image.ImageView;
import stratmaker.java.global.Global;
import stratmaker.java.undo.Undoable;

public class DeleteNodeAction implements Action, Undoable
{
	private ImageView imgView;
	
	public DeleteNodeAction()
	{
		reset();
	}
	
	public DeleteNodeAction(DeleteNodeAction removeNodeAction)
	{
		this.imgView = removeNodeAction.getImageView();
	}
	
	public DeleteNodeAction(ImageView img)
	{
		this.imgView = img;
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
		Global.makerController.getCurrentAnchorPane().getChildren().add(imgView);
	}

	@Override
	public void redo()
	{
		Global.makerController.getCurrentAnchorPane().getChildren().remove(imgView);
	}

	@Override
	public Undoable copy()
	{
		return new DeleteNodeAction(this);
	}

	@Override
	public void execute()
	{
		Global.makerController.getCurrentAnchorPane().getChildren().remove(imgView);
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
	}

}
