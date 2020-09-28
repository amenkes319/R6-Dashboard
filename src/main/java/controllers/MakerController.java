package main.java.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.java.Floor;
import main.java.Global;
import main.java.Map;
import main.java.actions.MoveNodeAction;
import main.java.undo.UndoCollector;

public class MakerController
{
	@FXML private TabPane tabPane;
	@FXML private ArrayList<Button> opList, gadgetList;
	@FXML private RadioButton dragRadio, rotateRadio, drawRadio;
	@FXML private ColorPicker colorPicker;
	@FXML private MenuItem undoBtn, redoBtn;
	
	private Map selectedMap;
	private Floor[] floors;
	
	private MoveNodeAction moveNodeAction;
	
	public void changeToScene(Map selectedMap)
	{
		this.selectedMap = selectedMap;

		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/fxml/Maker.fxml"));

            loader.setController(this);
            Global.primaryStage.setResizable(true);
            Global.primaryStage.setScene(new Scene(loader.load()));
            Global.primaryStage.centerOnScreen();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

		init();
	}
	
	private void init()
	{
		addFloors();
		drawInit();
		
		moveNodeAction = new MoveNodeAction();
		
		for (Button button : this.opList)
		{
			button.setOnAction(event -> addNode(event));
			ImageView imgView = new ImageView(new Image("/main/resources/Operators/Defenders/" + button.getText().toLowerCase() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/main/css/Selection.css");
		}
		
		for (Button button : this.gadgetList)
		{
			button.setOnAction(event -> addNode(event));
			ImageView imgView = new ImageView(new Image("/main/resources/Gadgets/" + button.getText().toLowerCase() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/main/css/Selection.css");
		}
		
		drawRadio.setOnAction(e -> 
		{
			getCurrentFloor().getCanvas().setWidth(getCurrentAnchorPane().getWidth());
			getCurrentFloor().getCanvas().setHeight(getCurrentAnchorPane().getHeight());
		});
		
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() 
	    {
	        @Override
	        public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) 
	        {
	            for (Floor floor : floors)
	            {
	            	if (floor.toString().equals(newTab.getText()))
	            	{
	            		AnchorPane anchorPane = ((AnchorPane) ((ScrollPane) newTab.getContent()).getContent());
	            		floor.getCanvas().setWidth(anchorPane.getWidth());
	            		floor.getCanvas().setHeight(anchorPane.getHeight());
	            		floor.getGC().setStroke(colorPicker.getValue());
	            	}
	            }
	        }
	    });

		undoBtn.setOnAction(e -> UndoCollector.INSTANCE.undo());
		redoBtn.setOnAction(e -> UndoCollector.INSTANCE.redo());
	}

	private void addFloors()
	{
		if (this.selectedMap == Map.CLUBHOUSE)
		{
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		}
		else if (this.selectedMap == Map.COASTLINE)
		{
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		}
		else if (this.selectedMap == Map.CONSULATE)
		{
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		}
		else if (this.selectedMap == Map.KAFE)
		{
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("3rd Floor"));
		}
		else if (this.selectedMap == Map.OREGON)
		{
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("T3"));
		}
		else if (this.selectedMap == Map.THEMEPARK)
		{
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		}
		else if (this.selectedMap == Map.VILLA)
		{
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		}
		
		this.floors = new Floor[tabPane.getTabs().size()];
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
		{
			ScrollPane scrollPane = new ScrollPane();
			String floor = tabPane.getTabs().get(i).getText();
			Image img = new Image("/main/resources/Blueprints/" + this.selectedMap.toString() + "/" + floor + ".jpg");
			
			floors[i] = new Floor(img, floor, 0, 0);
			
			AnchorPane anchorPane = new AnchorPane();
			anchorPane.getChildren().add(floors[i].getFloorView());
			scrollPane.setContent(anchorPane);
			tabPane.getTabs().get(i).setContent(scrollPane);
		}
	}
	
	public void addNode(ActionEvent event)
	{
		Image img = ((ImageView) ((Button) event.getSource()).getGraphic()).getImage();
		ImageView view = new ImageView(img);
		double imgRatio = img.getWidth() / img.getHeight();

		view.setFitHeight(75);
		view.setFitWidth(view.getFitHeight() * imgRatio);
		view.setOnMousePressed(e -> onImagePressed(view, e));
		view.setOnMouseDragged(e -> onImageDragged(view, e));
		view.setOnMouseReleased(e -> onImageReleased(view));

		view.setX(getCurrentAnchorPane().getWidth() / 2);
		view.setY(getCurrentAnchorPane().getHeight() / 2);
		
		Floor currentFloor = getCurrentFloor();
		
		currentFloor.addNode(view);
		
		getCurrentAnchorPane().getChildren().add(currentFloor.getNodes().get(currentFloor.getNodes().size()-1));
	}
	
	private double deltaX, deltaY, initX, initY;
	
	private void onImagePressed(ImageView imgView, MouseEvent event)
	{
		deltaX = event.getX() - imgView.getX();
		deltaY = event.getY() - imgView.getY();
		initX = event.getX();
		initY = event.getY();
		
		moveNodeAction.setImageView(imgView);
		moveNodeAction.setOldX(initX);
		moveNodeAction.setOldY(initY);
	}
	
	private void onImageDragged(ImageView imgView, MouseEvent event)
	{
		double mouseX = event.getX();
		double mouseY = event.getY();
		
		if (rotateRadio.isSelected())
		{
			// rotation (WIP)
//			double opp = (mouseX - initX - imgView.getFitWidth() / 2);
//			double adj = -(mouseY - initY - imgView.getFitHeight() / 2);
//			double hyp = Math.sqrt(opp*opp + adj*adj);
//			double sinAngle = Math.asin(adj / hyp);
//			double cosAngle = Math.acos(opp / hyp);
//			double angle = 0;
//			
//			if (sinAngle > 0 && sinAngle < Math.PI / 2)
//				angle = cosAngle;
//			else if (sinAngle < 0 && sinAngle > -Math.PI / 2)
//				angle = 2 * Math.PI - cosAngle;
//			
//			System.out.println(angle * 180 / Math.PI);
//			System.out.println(mouseY + "  " + imgView.getY() + "  " + imgView.getFitHeight() / 2 + "  " + adj);
//			System.out.println(opp + "  " + adj + "  " + hyp + "  " + angle * 180 / Math.PI);
//			imgView.setRotate(angle * 180 / Math.PI);
//			System.out.println(imgView.getRotate());
			System.out.println("ROTATE");
		}
		else if (dragRadio.isSelected())
		{
			moveNodeAction.setNewX(mouseX - deltaX);
			moveNodeAction.setNewY(mouseY - deltaY);
			moveNodeAction.execute();
		}
	}
	
	private void onImageReleased(ImageView imgView)
	{	
		moveNodeAction.setNewX(imgView.getX());
		moveNodeAction.setNewY(imgView.getY());
		UndoCollector.INSTANCE.add(moveNodeAction);
		
		moveNodeAction.reset();
	}
	
	public void drawInit()
	{
		colorPicker.setValue(Color.BLACK);
		colorPicker.getCustomColors().addAll(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE);
		colorPicker.setOnAction(e -> getCurrentFloor().getGC().setStroke(colorPicker.getValue()));
		
		for (Floor floor : floors)
			floor.initDraw();
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
			((AnchorPane) ((ScrollPane) tabPane.getTabs().get(i).getContent()).getContent()).getChildren().add(floors[i].getCanvas());
	}
	
	public boolean drawSelected()
	{
		return drawRadio.isSelected();
	}
	
	public ColorPicker getColorPicker()
	{
		return this.colorPicker;
	}
	
	private AnchorPane getCurrentAnchorPane()
	{
		return (AnchorPane) getCurrentScrollPane().getContent();
	}
	
	private ScrollPane getCurrentScrollPane()
	{
		return (ScrollPane) getCurrentTab().getContent();
	}
	
	private Tab getCurrentTab()
	{
		return this.tabPane.getSelectionModel().getSelectedItem();
	}
	
	private Floor getCurrentFloor()
	{
		return floors[this.tabPane.getSelectionModel().getSelectedIndex()];
	}
}
