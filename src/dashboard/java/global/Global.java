package dashboard.java.global;

import dashboard.java.controllers.MakerController;
import dashboard.java.controllers.SelectionController;
import javafx.stage.Stage;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selectionController = new SelectionController();
	public static MakerController maker = new MakerController();
}
