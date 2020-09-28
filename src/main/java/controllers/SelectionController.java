package main.java.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.Global;
import main.java.Map;

public class SelectionController
{
	@FXML private VBox parentVBox;
	@FXML private HBox topHBox, midHBox, bottomHBox;
	@FXML private Button clubBtn, coastBtn, consulateBtn, kafeBtn, oregonBtn, themeBtn, villaBtn;
	
	public void changeToScene()
	{
		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/fxml/Selection.fxml"));

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
		this.topHBox.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.topHBox.prefHeightProperty().bind(this.parentVBox.heightProperty());

		this.midHBox.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.midHBox.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.bottomHBox.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.bottomHBox.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.clubBtn.setOnAction(e -> Global.makerController.changeToScene(Map.CLUBHOUSE));
		this.clubBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.clubBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		this.clubBtn.setGraphic(new ImageView("/main/resources/Maps/Clubhouse.png"));
		this.clubBtn.getStylesheets().add("/main/css/Selection.css");
		this.clubBtn.getGraphic().prefWidth(this.clubBtn.getWidth());
		this.clubBtn.getGraphic().prefHeight(this.clubBtn.getHeight());
		
		this.coastBtn.setOnAction(e -> Global.makerController.changeToScene(Map.COASTLINE));
		this.coastBtn.getStylesheets().add("/main/css/Selection.css");
		this.coastBtn.setGraphic(new ImageView("/main/resources/Maps/Coastline.png"));
		this.coastBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.coastBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.consulateBtn.setOnAction(e -> Global.makerController.changeToScene(Map.CONSULATE));
		this.consulateBtn.getStylesheets().add("/main/css/Selection.css");
		this.consulateBtn.setGraphic(new ImageView("/main/resources/Maps/Consulate.png"));
		this.consulateBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.consulateBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.kafeBtn.setOnAction(e -> Global.makerController.changeToScene(Map.KAFE));
		this.kafeBtn.getStylesheets().add("/main/css/Selection.css");
		this.kafeBtn.setGraphic(new ImageView("/main/resources/Maps/Kafe.png"));
		this.kafeBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.kafeBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.oregonBtn.setOnAction(e -> Global.makerController.changeToScene(Map.OREGON));
		this.oregonBtn.getStylesheets().add("/main/css/Selection.css");
		this.oregonBtn.setGraphic(new ImageView("/main/resources/Maps/Oregon.png"));
		this.oregonBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.oregonBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.themeBtn.setOnAction(e -> Global.makerController.changeToScene(Map.THEMEPARK));
		this.themeBtn.getStylesheets().add("/main/css/Selection.css");
		this.themeBtn.setGraphic(new ImageView("/main/resources/Maps/Themepark.png"));
		this.themeBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.themeBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
		this.villaBtn.setOnAction(e -> Global.makerController.changeToScene(Map.VILLA));
		this.villaBtn.getStylesheets().add("/main/css/Selection.css");
		this.villaBtn.setGraphic(new ImageView("/main/resources/Maps/Villa.png"));
//		this.villaBtn.prefWidth(this.oregonBtn.getWidth());
//		this.villaBtn.prefHeight(this.oregonBtn.getHeight());
		this.villaBtn.prefWidthProperty().bind(this.parentVBox.widthProperty());
		this.villaBtn.prefHeightProperty().bind(this.parentVBox.heightProperty());
		
	}
	

}
