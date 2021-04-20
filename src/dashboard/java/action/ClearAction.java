package dashboard.java.action;

import dashboard.java.ZoomPane;
import dashboard.java.global.Global;
import dashboard.java.undo.Undoable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;

public class ClearAction implements Action, Undoable
{
	private ZoomPane oldPane, newPane;

	public ClearAction()
	{
		reset();
	}
	
	public ClearAction(ZoomPane oldPane, ZoomPane newPane)
	{
		this.oldPane = oldPane;
		this.newPane = newPane;
	}
	
	public ClearAction(ClearAction clearAction)
	{
		this.oldPane = clearAction.getOldPane();
		this.newPane = clearAction.getNewPane();
	}
	
	public ZoomPane getOldPane()
	{
		return this.oldPane;
	}
	
	public void setOldPane(ZoomPane oldPane)
	{
		this.oldPane = oldPane;
	}
	
	public ZoomPane getNewPane()
	{
		return this.newPane;
	}
	
	@Override
	public void undo()
	{
		Global.maker.setCurrentPane(oldPane);
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
		Canvas oldCanvas = (Canvas) oldPane.getChildren().get(1);
		Canvas newCanvas = new Canvas(oldCanvas.getWidth(), oldCanvas.getHeight());
		ImageView map = new ImageView(((ImageView) oldPane.getChildren().get(0)).getImage());
		newPane = new ZoomPane(map, newCanvas);
		newPane.setScale(oldPane.getScale());
		newPane.setTranslateX(oldPane.getTranslateX());
		newPane.setTranslateY(oldPane.getTranslateY());
		System.out.println(newCanvas.getWidth());
		Global.maker.setCurrentPane(newPane);
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
