package stratmaker.java;
	
import javafx.application.Application;
import javafx.stage.Stage;
import stratmaker.java.global.Global;

public class Main extends Application 
{
	@Override
	public void start(Stage primaryStage) 
	{
		Global.primaryStage = primaryStage;
        Global.primaryStage.setTitle("R6 Strat Maker");
		Global.selectionController.changeToScene();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
