package dashboard.java;
	
import javafx.application.Application;
import javafx.stage.Stage;
import dashboard.java.global.Global;

public class Dashboard extends Application 
{
	public void start(Stage primaryStage) 
	{
		Global.primaryStage = primaryStage;
        Global.primaryStage.setTitle("R6 Dashboard");
		Global.selectionController.changeToScene();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
