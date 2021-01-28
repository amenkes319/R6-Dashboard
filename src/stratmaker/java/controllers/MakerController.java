package stratmaker.java.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import stratmaker.java.Map;
import stratmaker.java.actions.AddNodeAction;
import stratmaker.java.actions.ClearAction;
import stratmaker.java.actions.DeleteNodeAction;
import stratmaker.java.actions.MoveNodeAction;
import stratmaker.java.actions.RotateNodeAction;
import stratmaker.java.floor.Floor;
import stratmaker.java.global.Global;
import stratmaker.java.undo.UndoCollector;
import stratmaker.java.zoom.ZoomScrollPane;

public class MakerController
{
	@FXML private BorderPane borderPane;
	@FXML private TabPane tabPane;
	@FXML private ArrayList<Button> opList, gadgetList;
	@FXML private Button backBtn, clearBtn;
	@FXML private RadioButton dragRadio, rotateRadio, drawRadio, eraseRadio, deleteRadio;
	@FXML private ColorPicker colorPicker;
	@FXML private Slider lineWidthSlider;
	@FXML private MenuItem undoBtn, redoBtn;
	
	private Map selectedMap;
	private Floor[] floors;
	
	private AddNodeAction addNodeAction;
	private MoveNodeAction moveNodeAction;
	private RotateNodeAction rotateNodeAction;
	private ClearAction clearAction;
	private DeleteNodeAction deleteNodeAction;
	
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
		deleteNodeAction = new DeleteNodeAction();
		
		String path = System.getProperty("user.dir") + "/src/stratmaker/resources/Nodes/";
		addTab(new File(path + "Ops/Def"));
//		addTab(new File(path + "Ops/Atk"));
		addTab(new File(path + "Gadgets/Def"));
//		addTab(new File(path + "Gadgets/Atk"));
		
		backBtn.setOnAction(e -> Global.selectionController.changeToScene());
		
		clearBtn.setOnAction(e ->
		{
			clearAction.setOldAnchorPane(getCurrentAnchorPane());
			clearAction.execute();
			UndoCollector.INSTANCE.add(clearAction);
			clearAction.reset();
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
		
		Global.primaryStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, key ->
		{
			if (key.isControlDown())
			{
				if (key.getCode() == KeyCode.Z)
					undoBtn.fire();
				
				if (key.getCode() == KeyCode.Y)
					redoBtn.fire();
			}
			
		});
	}

	private void addFloors()
	{
		if (selectedMap == Map.CLUBHOUSE)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (selectedMap == Map.COASTLINE)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (selectedMap == Map.CONSULATE)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (selectedMap == Map.KAFE)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("3rd Floor"));
		else if (selectedMap == Map.OREGON)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("T3"));
		else if (selectedMap == Map.THEMEPARK)
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (selectedMap == Map.VILLA)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		
		this.floors = new Floor[tabPane.getTabs().size()];
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
		{
			String floor = tabPane.getTabs().get(i).getText();
			Image img = new Image("/stratmaker/resources/Blueprints/" + this.selectedMap.toString() + "/" + floor + ".jpg");
			
			floors[i] = new Floor(img, floor);
			
			AnchorPane anchorPane = new AnchorPane();
			ZoomScrollPane scrollPane = new ZoomScrollPane(anchorPane);
			anchorPane.getChildren().add(floors[i].getImgView());
			tabPane.getTabs().get(i).setContent(scrollPane);
		}
	}
	
	public void addTab(File dir)
	{
		Tab tab = new Tab(dir.getName() + " " + dir.getParentFile().getName());
		ScrollPane sp = new ScrollPane();
		VBox vb = new VBox();
		TextField search = new TextField();
		search.setPromptText("Search");
		search.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		    for (Node n : vb.getChildren())
		    {
		    	if (n instanceof Button)
		    	{
			    	n.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(((Button) n).getText()).find());
			    	n.setManaged(n.isVisible());
		    	}
		    }
		});
		
		vb.getChildren().add(search);
		vb.setSpacing(10);
		vb.setStyle("-fx-background-color: #3f3f3f");
		sp.setContent(vb);
		tab.setContent(sp);
		
		for (File file : dir.listFiles())
		{
			String name = file.getName().substring(0, file.getName().length() - 4);
			Button button = new Button(name);
			button.setOnAction(e -> addNode(e));
			button.setTextFill(Color.WHITE);
			button.setPrefWidth(180);
			button.setPrefHeight(64);
			button.setAlignment(Pos.CENTER_LEFT);
			button.setStyle("-fx-font-size:16");
			
			try
			{
				ImageView imgView = new ImageView(file.toURI().toURL().toString());
				imgView.setFitHeight(56);
				imgView.setFitWidth(60);
				button.setGraphic(imgView);
			} 
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			
			button.getStylesheets().add("/stratmaker/css/Maker.css");
			vb.getChildren().add(button);
		}

		((TabPane) borderPane.getRight()).setPrefWidth(195);
		((TabPane) borderPane.getRight()).getTabs().add(tab);
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
		imgView.setOnMouseClicked(e -> onImageClicked(e));
		
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
		if (!deleteRadio.isSelected())
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
				
				theta = (theta + Math.PI) * 180 / Math.PI;
				
				//Theta value becomes 0-2pi
				if (event.isControlDown())
				{
					if (theta % 45 < 23)
						theta = 45 * ((int) (theta / 45));
					else
						theta = 45 * ((int) (theta / 45) + 1);
				}
				rotateNodeAction.setNewTheta(theta);
				rotateNodeAction.execute();
			}
			else if (dragRadio.isSelected() && moveNodeAction.canExecute())
			{
				moveNodeAction.setNewX(scrollXPosition(mouseX) - xOffset);
				moveNodeAction.setNewY(scrollYPosition(mouseY) - yOffset - 112);
				moveNodeAction.execute();
//				System.out.println(moveNodeAction.getNewX() + " " + moveNodeAction.getNewY());
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
	
	private void onImageClicked(MouseEvent event)
	{
		if (deleteRadio.isSelected())
		{
			ImageView imgView = (ImageView) event.getSource();
			deleteNodeAction.setImageView(imgView);
			deleteNodeAction.execute();
			UndoCollector.INSTANCE.add(deleteNodeAction);
			deleteNodeAction.reset();
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
