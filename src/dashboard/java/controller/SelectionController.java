package dashboard.java.controller;

import java.io.IOException;

import dashboard.java.Map;
import dashboard.java.global.Global;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SelectionController
{
	@FXML private VBox parentVBox;
	@FXML private HBox topHBox, midHBox, bottomHBox;
	@FXML private Button chaletBtn, clubBtn, coastBtn, consulateBtn, kafeBtn, oregonBtn, themeBtn, villaBtn;
	
	public void show()
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
		chaletBtn.setOnAction(e -> Global.maker.show(Map.CHALET));
		chaletBtn.getStylesheets().add("/dashboard/css/Selection.css");
		chaletBtn.setText("Chalet");
		chaletBtn.setTextFill(Color.WHITE);
//		chaletBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Chalet.png"));

		clubBtn.setOnAction(e -> Global.maker.show(Map.CLUBHOUSE));
		clubBtn.getStylesheets().add("/dashboard/css/Selection.css");
		clubBtn.setText("Club");
		clubBtn.setTextFill(Color.WHITE);
//		clubBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Clubhouse.png"));
		
		coastBtn.setOnAction(e -> Global.maker.show(Map.COASTLINE));
		coastBtn.getStylesheets().add("/dashboard/css/Selection.css");
		coastBtn.setText("Coast");
		coastBtn.setTextFill(Color.WHITE);
//		coastBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Coastline.png"));
		
		consulateBtn.setOnAction(e -> Global.maker.show(Map.CONSULATE));
		consulateBtn.getStylesheets().add("/dashboard/css/Selection.css");
		consulateBtn.setText("Consulate");
		consulateBtn.setTextFill(Color.WHITE);
//		consulateBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Consulate.png"));
		
		kafeBtn.setOnAction(e -> Global.maker.show(Map.KAFE));
		kafeBtn.getStylesheets().add("/dashboard/css/Selection.css");
		kafeBtn.setText("Kafe");
		kafeBtn.setTextFill(Color.WHITE);
//		kafeBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Kafe.png"));
		
		oregonBtn.setOnAction(e -> Global.maker.show(Map.OREGON));
		oregonBtn.getStylesheets().add("/dashboard/css/Selection.css");
		oregonBtn.setText("Oregon");
		oregonBtn.setTextFill(Color.WHITE);
//		oregonBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Oregon.png"));
		
		villaBtn.setOnAction(e -> Global.maker.show(Map.VILLA));
		villaBtn.getStylesheets().add("/dashboard/css/Selection.css");
		villaBtn.setText("Villa");
		villaBtn.setTextFill(Color.WHITE);
//		villaBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Villa.png"));
		
	}
	

}
