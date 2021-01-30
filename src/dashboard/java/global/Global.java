package dashboard.java.global;

import dashboard.java.controllers.MakerController;
import javafx.stage.Stage;
import dashboard.java.controllers.SelectionController;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selectionController = new SelectionController();
	public static MakerController makerController = new MakerController();
}
