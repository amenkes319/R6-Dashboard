package dashboard.java.global;

import dashboard.java.controllers.MakerController;
import dashboard.java.controllers.SelectionController;
import dashboard.java.controllers.SettingsController;
import javafx.stage.Stage;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selection = new SelectionController();
	public static MakerController maker = new MakerController();
	public static SettingsController settings = new SettingsController();
}
