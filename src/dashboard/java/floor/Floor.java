package dashboard.java.floor;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import dashboard.java.actions.DrawAction;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;

public class Floor
{
	private ImageView floorView;
	private Canvas canvas;
	private GraphicsContext gc;
	private String name;
	
	private DrawAction drawAction;
	
	private boolean bDraw;
	
	public Floor(Image floor, String name)
	{
		this.floorView = new ImageView(floor);
		this.canvas = new Canvas(floorView.getFitWidth(), floorView.getFitHeight());
		this.gc = canvas.getGraphicsContext2D();
		this.name = name;
		
		this.drawAction = new DrawAction();

		this.bDraw = false;
	}
	
	public void drawInit()
	{
		this.gc.setStroke(Global.makerController.getColorPicker().getValue());
		gc.setLineCap(StrokeLineCap.ROUND);
	    gc.setLineJoin(StrokeLineJoin.ROUND);
		gc.setLineWidth(Global.makerController.getLineWidthSlider().getValue());
		
		canvas.setOnMousePressed(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				bDraw = true;
				drawAction.setOldX(e.getX());
				drawAction.setOldY(e.getY());
				drawAction.setOldCanvas(copyCanvas());
			}
		});
		
		canvas.setOnMouseDragged(e ->
		{
			if (bDraw && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				drawAction.setX(e.getX());
				drawAction.setY(e.getY());
				drawAction.execute();
			}
		});

		canvas.setOnMouseReleased(e ->
		{
			if (e.getButton() == MouseButton.PRIMARY && (Global.makerController.drawSelected() || Global.makerController.eraseSelected()))
			{
				bDraw = false;
				drawAction.setNewCanvas(canvas);

				UndoCollector.INSTANCE.add(drawAction);
				drawAction.reset();
			}
		});
	}
	
	private Canvas copyCanvas()
	{
		Canvas copyCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
		SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT); 
        WritableImage image = canvas.snapshot(params, null);
        copyCanvas.getGraphicsContext2D().drawImage(image, 0, 0);
        return copyCanvas;
	}
	
	public ImageView getImgView()
	{
		return this.floorView;
	}
	
	public Canvas getCanvas()
	{
		return this.canvas;
	}
	
	public void setCanvas(Canvas canvas)
	{
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		this.drawAction = new DrawAction();
		drawInit();
	}
	
	public GraphicsContext getGC()
	{
		return this.gc;
	}
	
	public String toString()
	{
		return this.name;
	}
}


