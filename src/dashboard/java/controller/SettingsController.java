package dashboard.java.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsController
{
	private Stage settingsStage;

	public void show()
	{
		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/Settings.fxml"));

            loader.setController(this);
            settingsStage = new Stage();
            settingsStage.setResizable(false);
            settingsStage.setScene(new Scene(loader.load()));
            settingsStage.centerOnScreen();
            settingsStage.showAndWait();
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
