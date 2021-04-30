package dashboard.java.global;

import dashboard.java.controller.MakerController;
import dashboard.java.controller.SelectionController;
import dashboard.java.controller.SettingsController;
import dashboard.java.model.Data;
import javafx.stage.Stage;

public class Global
{
	public static Stage primaryStage;
	
	public final static SelectionController selection = new SelectionController();
	public final static MakerController maker = new MakerController();
	public final static SettingsController settings = new SettingsController();
	public final static Data data = new Data();
}
