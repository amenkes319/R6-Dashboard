package stratmaker.java.actions;

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
	}

	@Override
	public void redo()
	{
		Global.makerController.setCurrentAnchorPane(newPane);
	}

	@Override
	public Undoable copy()
	{
		ClearAction clearAction = new ClearAction(this);
		return clearAction;
	}

	@Override
	public void execute()
	{
		newPane = new AnchorPane(oldPane.getChildren().get(0));
		Global.makerController.setCurrentAnchorPane(newPane);
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
