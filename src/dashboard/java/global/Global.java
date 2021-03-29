package dashboard.java.global;

import java.util.LinkedList;

import dashboard.java.controllers.MakerController;
import dashboard.java.controllers.SelectionController;
import dashboard.java.controllers.SettingsController;
import dashboard.java.model.Line;
import javafx.stage.Stage;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selection = new SelectionController();
	public static MakerController maker = new MakerController();
	public static SettingsController settings = new SettingsController();
}
