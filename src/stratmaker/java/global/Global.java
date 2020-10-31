package stratmaker.java.global;

import javafx.stage.Stage;
import stratmaker.java.controllers.MakerController;
import stratmaker.java.controllers.SelectionController;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selectionController = new SelectionController();
	public static MakerController makerController = new MakerController();
}
