package dashboard.java;
	
import dashboard.java.global.Global;
import javafx.application.Application;
import javafx.stage.Stage;

public class Dashboard extends Application 
{
	public void start(Stage primaryStage) 
	{
		Global.primaryStage = primaryStage;
        Global.primaryStage.setTitle("R6 Dashboard - v0.3.0.3");
		Global.selection.show();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
