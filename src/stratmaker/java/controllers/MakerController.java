package stratmaker.java.controllers;

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
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import stratmaker.java.Map;
import stratmaker.java.actions.AddNodeAction;
import stratmaker.java.actions.ClearAction;
import stratmaker.java.actions.MoveNodeAction;
import stratmaker.java.actions.RotateNodeAction;
import stratmaker.java.floor.Floor;
import stratmaker.java.global.Global;
import stratmaker.java.undo.UndoCollector;

public class MakerController
{
	@FXML private TabPane tabPane;
	@FXML private ArrayList<Button> opList, gadgetList;
	@FXML private Button backBtn, clearBtn;
	@FXML private RadioButton dragRadio, rotateRadio, drawRadio, eraseRadio;
	@FXML private ColorPicker colorPicker;
	@FXML private Slider lineWidthSlider;
	@FXML private MenuItem undoBtn, redoBtn;
	
	private Map selectedMap;
	private Floor[] floors;
	
	private AddNodeAction addNodeAction;
	private MoveNodeAction moveNodeAction;
	private RotateNodeAction rotateNodeAction;
	private ClearAction clearAction;
	
	public void changeToScene(Map selectedMap)
	{
		this.selectedMap = selectedMap;

		try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stratmaker/resources/fxml/Maker.fxml"));

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
		lineWidthSlider.setValue(5.0);
		lineWidthSlider.valueProperty().addListener(new ChangeListener<Number>() 
		{
			public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
			{
				for (Floor floor : floors)
					floor.getGC().setLineWidth(lineWidthSlider.getValue());
			}
		});
		
		addFloors();
		drawInit();
		
		moveNodeAction = new MoveNodeAction();
		addNodeAction = new AddNodeAction();
		rotateNodeAction = new RotateNodeAction();
		clearAction = new ClearAction();
		
		for (Button button : this.opList)
		{
			button.setOnAction(e -> addNode(e));
			ImageView imgView = new ImageView(new Image("/stratmaker/resources/Operators/Defenders/" + button.getText().toLowerCase() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/stratmaker/css/Maker.css");
		}
		
		for (Button button : this.gadgetList)
		{
			button.setOnAction(e -> addNode(e));
			ImageView imgView = new ImageView(new Image("/stratmaker/resources/Gadgets/" + button.getText().toLowerCase() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/stratmaker/css/Maker.css");
		}
		
		backBtn.setOnAction(e -> Global.selectionController.changeToScene());
		clearBtn.setOnAction(e ->
		{
//			System.out.println(getCurrentAnchorPane().getChildren());
			getCurrentAnchorPane().getChildren().remove(2, getCurrentAnchorPane().getChildren().size());
//			clearAction.setOldAnchorPane(getCurrentAnchorPane());
//			clearAction.execute();
//			UndoCollector.INSTANCE.add(clearAction);
//			clearAction.reset();
		});
		
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
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (this.selectedMap == Map.COASTLINE)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (this.selectedMap == Map.CONSULATE)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (this.selectedMap == Map.KAFE)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("3rd Floor"));
		else if (this.selectedMap == Map.OREGON)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("T3"));
		else if (this.selectedMap == Map.THEMEPARK)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (this.selectedMap == Map.VILLA)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		
		this.floors = new Floor[tabPane.getTabs().size()];
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
		{
			ScrollPane scrollPane = new ScrollPane();
			String floor = tabPane.getTabs().get(i).getText();
			Image img = new Image("/stratmaker/resources/Blueprints/" + this.selectedMap.toString() + "/" + floor + ".jpg");
			
			floors[i] = new Floor(img, floor);
			
			AnchorPane anchorPane = new AnchorPane();
			anchorPane.getChildren().add(floors[i].getFloorView());
			scrollPane.setContent(anchorPane);
			tabPane.getTabs().get(i).setContent(scrollPane);
		}
	}
	
	public void addNode(ActionEvent event)
	{
		Image img = ((ImageView) ((Button) event.getSource()).getGraphic()).getImage();
		ImageView imgView = new ImageView(img);
		double imgRatio = img.getWidth() / img.getHeight();

		imgView.setFitHeight(75);
		imgView.setFitWidth(imgView.getFitHeight() * imgRatio);
		imgView.setOnMousePressed(e -> onImagePressed(e));
		imgView.setOnMouseDragged(e -> onImageDragged(e));
		imgView.setOnMouseReleased(e -> onImageReleased(e));
		
		imgView.setX(scrollXPosition(getCurrentScrollPane().getWidth() / 2 - imgView.getFitWidth()));
		imgView.setY(scrollYPosition(getCurrentScrollPane().getHeight() / 2 - imgView.getFitHeight()));
			
		addNodeAction.setImageView(imgView);
		if (addNodeAction.canExecute())
		{
			addNodeAction.execute();

			UndoCollector.INSTANCE.add(addNodeAction);
			addNodeAction.reset();
		}
	}
	
	private double initX, initY, xOffset, yOffset;
	
	private void onImagePressed(MouseEvent event)
	{
		ImageView imgView = (ImageView) event.getSource();
		initX = event.getSceneX();
		initY = event.getSceneY();
		xOffset = event.getX() - imgView.getX();
		yOffset = event.getY() - imgView.getY();
		
		System.out.println(event.getY() + "  " + imgView.getY());
		
		moveNodeAction.setImageView(imgView);
		moveNodeAction.setOldX(event.getSceneX());
		moveNodeAction.setOldY(event.getSceneY());
		
		rotateNodeAction.setImageView(imgView);
		rotateNodeAction.setOldTheta(imgView.getRotate());
	}
	
	private void onImageDragged(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
		{
			double mouseX = event.getSceneX();
			double mouseY = event.getSceneY();
			if (rotateRadio.isSelected())
			{
				double deltaX = event.getSceneX() - initX;
				double deltaY = event.getSceneY() - initY;
				double theta = Math.atan(deltaY/deltaX) + Math.PI / 2;
				
				if (deltaX == 0 && deltaY == 0)
				{
					theta = 0;
				}
				else if (deltaX >= 0)
				{
					theta += Math.PI;
				}
				
				rotateNodeAction.setNewTheta((theta + Math.PI) * 180 / Math.PI);
				rotateNodeAction.execute();
				
				System.out.println(deltaX + "  " + deltaY + "  " + theta * 180 / Math.PI);
			}
			else if (dragRadio.isSelected() && moveNodeAction.canExecute())
			{
				moveNodeAction.setNewX(scrollXPosition(mouseX) - xOffset);
				moveNodeAction.setNewY(scrollYPosition(mouseY) - yOffset - 112);
				moveNodeAction.execute();
			}
		}
	}
	
	private void onImageReleased(MouseEvent event)
	{
		ImageView imgView = (ImageView) event.getSource();
		if (event.getButton() == MouseButton.PRIMARY)
		{
			if (dragRadio.isSelected())
			{
				moveNodeAction.setNewX(imgView.getX());
				moveNodeAction.setNewY(imgView.getY());
				UndoCollector.INSTANCE.add(moveNodeAction);
				
				moveNodeAction.reset();
			}
			else if (rotateRadio.isSelected())
			{
				System.out.println(imgView.getRotate());
				rotateNodeAction.setNewTheta(imgView.getRotate());
				UndoCollector.INSTANCE.add(rotateNodeAction);
				
				rotateNodeAction.reset();
			}
		}
	}
	
	public void drawInit()
	{
		colorPicker.setValue(Color.BLACK);
		colorPicker.getCustomColors().addAll(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE);
		colorPicker.setOnAction(e -> getCurrentFloor().getGC().setStroke(colorPicker.getValue()));
		
		for (Floor floor : floors)
			floor.drawInit();
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
		{
			ScrollPane sPane = (ScrollPane) tabPane.getTabs().get(i).getContent();
			AnchorPane aPane = (AnchorPane) sPane.getContent();
			aPane.getChildren().add(floors[i].getCanvas());
		}
	}
	
	private double scrollXPosition(double x)
	{
		double hScrollPosition = getCurrentScrollPane().getHvalue();
		double anchorPaneWidth = getCurrentAnchorPane().getWidth();
		double scrollPaneWidth = getCurrentScrollPane().getWidth();
		
		return hScrollPosition * (anchorPaneWidth - scrollPaneWidth) + x;
	}
	
	private double scrollYPosition(double y)
	{
		double vScrollPosition = getCurrentScrollPane().getVvalue();
		double anchorPaneHeight = getCurrentAnchorPane().getHeight();
		double scrollPaneHeight = getCurrentScrollPane().getHeight();
		
		return vScrollPosition * (anchorPaneHeight - scrollPaneHeight) + y;
	}
	
	public boolean drawSelected()
	{
		return drawRadio.isSelected();
	}
	
	public boolean eraseSelected()
	{
		return eraseRadio.isSelected();
	}
	
	public ColorPicker getColorPicker()
	{
		return this.colorPicker;
	}
	
	public Slider getLineWidthSlider()
	{
		return this.lineWidthSlider;
	}
	
	public AnchorPane getCurrentAnchorPane()
	{
		return (AnchorPane) getCurrentScrollPane().getContent();
	}
	
	public void setCurrentAnchorPane(AnchorPane anchorPane)
	{
		getCurrentScrollPane().setContent(anchorPane);
	}
	
	public ScrollPane getCurrentScrollPane()
	{
		return (ScrollPane) tabPane.getSelectionModel().getSelectedItem().getContent();
	}
	
	public Floor getCurrentFloor()
	{
		return floors[this.tabPane.getSelectionModel().getSelectedIndex()];
	}
}
