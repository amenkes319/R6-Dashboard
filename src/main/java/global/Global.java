package main.java.global;

import javafx.stage.Stage;
import main.java.controllers.MakerController;
import main.java.controllers.SelectionController;

public class Global
{
	public static Stage primaryStage;
	
	public static SelectionController selectionController = new SelectionController();
	public static MakerController makerController = new MakerController();
}
