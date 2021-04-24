package dashboard.java.node;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ImageNode extends ImageView
{
	private Rectangle border;

	public ImageNode()
	{
		super();
		border = new Rectangle(getFitWidth(), getFitHeight(), Color.TRANSPARENT);
		border.setStroke(Color.BLACK);
		border.setStrokeWidth(2.5);
		initListeners();
	}

	public ImageNode(String url)
	{
		this(new Image(url));
	}

	public ImageNode(Image image)
	{
		super(image);
		border = new Rectangle(getFitWidth(), getFitHeight(), Color.TRANSPARENT);
		border.setStroke(Color.BLACK);
		border.setStrokeWidth(2.5);
		initListeners();
	}
	
	private void initListeners()
	{
		fitWidthProperty().addListener((observable, oldValue, newValue) -> {
			border.setWidth(newValue.doubleValue());
		});
		fitHeightProperty().addListener((observable, oldValue, newValue) -> {
			border.setHeight(newValue.doubleValue());
		});
		translateXProperty().addListener((observable, oldValue, newValue) -> {
			border.setTranslateX(newValue.doubleValue());
		});
		translateYProperty().addListener((observable, oldValue, newValue) -> {
			border.setTranslateY(newValue.doubleValue());
		});
		rotateProperty().addListener((observable, oldValue, newValue) -> {
			border.setRotate(newValue.doubleValue());
		});
		scaleXProperty().addListener((observable, oldValue, newValue) -> {
			border.setScaleX(newValue.doubleValue());
		});
		scaleYProperty().addListener((observable, oldValue, newValue) -> {
			border.setScaleY(newValue.doubleValue());
		});
	}

	public Rectangle getBorder()
	{
		return border;
	}

	public void setBorder(Rectangle border)
	{
		this.border = border;
	}
}
