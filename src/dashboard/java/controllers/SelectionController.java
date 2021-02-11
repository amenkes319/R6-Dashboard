package dashboard.java.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import dashboard.java.Map;
import dashboard.java.global.Global;

public class SelectionController
{
	@FXML private VBox parentVBox;
	@FXML private HBox topHBox, midHBox, bottomHBox;
	@FXML private Button clubBtn, coastBtn, consulateBtn, kafeBtn, oregonBtn, themeBtn, villaBtn;
	
	public void changeToScene()
	{
		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/Selection.fxml"));

            loader.setController(this);
            Global.primaryStage.setResizable(true);
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
		topHBox.prefWidthProperty().bind(parentVBox.widthProperty());
		topHBox.prefHeightProperty().bind(parentVBox.heightProperty());

		midHBox.prefWidthProperty().bind(parentVBox.widthProperty());
		midHBox.prefHeightProperty().bind(parentVBox.heightProperty());
		
		bottomHBox.prefWidthProperty().bind(parentVBox.widthProperty());
		bottomHBox.prefHeightProperty().bind(parentVBox.heightProperty());
		
		clubBtn.setOnAction(e -> Global.maker.changeToScene(Map.CLUBHOUSE));
		clubBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		clubBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		clubBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Clubhouse.png"));
		clubBtn.getStylesheets().add("/dashboard/css/Selection.css");
		clubBtn.getGraphic().prefWidth(clubBtn.getWidth());
		clubBtn.getGraphic().prefHeight(clubBtn.getHeight());
		
		coastBtn.setOnAction(e -> Global.maker.changeToScene(Map.COASTLINE));
		coastBtn.getStylesheets().add("/dashboard/css/Selection.css");
		coastBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Coastline.png"));
		coastBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		coastBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
		consulateBtn.setOnAction(e -> Global.maker.changeToScene(Map.CONSULATE));
		consulateBtn.getStylesheets().add("/dashboard/css/Selection.css");
		consulateBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Consulate.png"));
		consulateBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		consulateBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
		kafeBtn.setOnAction(e -> Global.maker.changeToScene(Map.KAFE));
		kafeBtn.getStylesheets().add("/dashboard/css/Selection.css");
		kafeBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Kafe.png"));
		kafeBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		kafeBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
		oregonBtn.setOnAction(e -> Global.maker.changeToScene(Map.OREGON));
		oregonBtn.getStylesheets().add("/dashboard/css/Selection.css");
		oregonBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Oregon.png"));
		oregonBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		oregonBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
		themeBtn.setOnAction(e -> Global.maker.changeToScene(Map.THEMEPARK));
		themeBtn.getStylesheets().add("/dashboard/css/Selection.css");
		themeBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Themepark.png"));
		themeBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		themeBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
		villaBtn.setOnAction(e -> Global.maker.changeToScene(Map.VILLA));
		villaBtn.getStylesheets().add("/dashboard/css/Selection.css");
		villaBtn.setGraphic(new ImageView("/dashboard/resources/Maps/Villa.png"));
//		villaBtn.prefWidth(oregonBtn.getWidth());
//		villaBtn.prefHeight(oregonBtn.getHeight());
		villaBtn.prefWidthProperty().bind(parentVBox.widthProperty());
		villaBtn.prefHeightProperty().bind(parentVBox.heightProperty());
		
	}
	

}
