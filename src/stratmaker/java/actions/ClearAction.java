package stratmaker.java.actions;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import stratmaker.java.global.Global;
import stratmaker.java.undo.Undoable;

public class ClearAction implements Action, Undoable
{
	private AnchorPane oldPane, newPane;

	public ClearAction()
	{
		reset();
	}
	
	public ClearAction(AnchorPane oldPane, AnchorPane newPane)
	{
		this.oldPane = oldPane;
		this.newPane = newPane;
	}
	
	public ClearAction(ClearAction clearAction)
	{
		this.oldPane = clearAction.getOldAnchorPane();
		this.newPane = clearAction.getNewAnchorPane();
	}
	
	public AnchorPane getOldAnchorPane()
	{
		return this.oldPane;
	}
	
	public void setOldAnchorPane(AnchorPane oldPane)
	{
		this.oldPane = oldPane;
	}
	
	public AnchorPane getNewAnchorPane()
	{
		return this.newPane;
	}
	
	@Override
	public void undo()
	{
		Global.makerController.setCurrentAnchorPane(oldPane);
		Global.makerController.getCurrentFloor().setCanvas((Canvas) oldPane.getChildren().get(1));
	}

	@Override
	public void redo()
	{
		execute();
	}

	@Override
	public Undoable copy()
	{
		return new ClearAction(this);
	}

	@Override
	public void execute()
	{
		Canvas newCanvas = new Canvas(oldPane.getWidth(), oldPane.getHeight());
		ImageView map = new ImageView(((ImageView) oldPane.getChildren().get(0)).getImage());
		newPane = new AnchorPane(map, newCanvas);
		Global.makerController.setCurrentAnchorPane(newPane);
		Global.makerController.getCurrentFloor().setCanvas(newCanvas);
		
		
	}

	@Override
	public boolean canExecute()
	{
		return true;
	}

	@Override
	public void reset()
	{
		this.oldPane = null;
		this.newPane = null;
	}
}
