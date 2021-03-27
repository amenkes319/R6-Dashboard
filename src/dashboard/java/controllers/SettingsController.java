package dashboard.java.controllers;

import java.io.IOException;

import dashboard.java.global.Global;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class SettingsController
{

	public void show()
	{
		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/Settings.fxml"));

            loader.setController(this);
            Global.primaryStage.setResizable(false);
            Global.primaryStage.setScene(new Scene(loader.load()));
            Global.primaryStage.centerOnScreen();
            Global.primaryStage.show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

		init();
	}
	
	private void init()
	{
		
	}
}
