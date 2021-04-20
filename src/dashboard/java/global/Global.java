package dashboard.java.global;

import dashboard.java.controller.MakerController;
import dashboard.java.controller.SelectionController;
import dashboard.java.controller.SettingsController;
import dashboard.java.model.Data;
import javafx.stage.Stage;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selection = new SelectionController();
	public static MakerController maker = new MakerController();
	public static SettingsController settings = new SettingsController();
	public static Data data = new Data();
}
