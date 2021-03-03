package dashboard.java.controllers;

import java.io.IOException;

import dashboard.java.Map;
import dashboard.java.global.Global;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SelectionController
{
	@FXML private VBox parentVBox;
	@FXML private HBox topHBox, midHBox, bottomHBox;
	@FXML private Button chaletBtn, clubBtn, coastBtn, consulateBtn, kafeBtn, oregonBtn, themeBtn, villaBtn;
	
	public void changeToScene()
	{
		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/Selection.fxml"));

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
		chaletBtn.setOnAction(e -> Global.maker.changeToScene(Map.CHALET));
		chaletBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Chalet.png"));
		chaletBtn.getStylesheets().add("/dashboard/css/Selection.css");

		clubBtn.setOnAction(e -> Global.maker.changeToScene(Map.CLUBHOUSE));
		clubBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Clubhouse.png"));
		clubBtn.getStylesheets().add("/dashboard/css/Selection.css");
		
		coastBtn.setOnAction(e -> Global.maker.changeToScene(Map.COASTLINE));
		coastBtn.getStylesheets().add("/dashboard/css/Selection.css");
		coastBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Coastline.png"));
		
		consulateBtn.setOnAction(e -> Global.maker.changeToScene(Map.CONSULATE));
		consulateBtn.getStylesheets().add("/dashboard/css/Selection.css");
		consulateBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Consulate.png"));
		
		kafeBtn.setOnAction(e -> Global.maker.changeToScene(Map.KAFE));
		kafeBtn.getStylesheets().add("/dashboard/css/Selection.css");
		kafeBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Kafe.png"));
		
		oregonBtn.setOnAction(e -> Global.maker.changeToScene(Map.OREGON));
		oregonBtn.getStylesheets().add("/dashboard/css/Selection.css");
		oregonBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Oregon.png"));
		
		themeBtn.setOnAction(e -> Global.maker.changeToScene(Map.THEMEPARK));
		themeBtn.getStylesheets().add("/dashboard/css/Selection.css");
		themeBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Themepark.png"));
		
		villaBtn.setOnAction(e -> Global.maker.changeToScene(Map.VILLA));
		villaBtn.getStylesheets().add("/dashboard/css/Selection.css");
		villaBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Villa.png"));
		
	}
	

}
