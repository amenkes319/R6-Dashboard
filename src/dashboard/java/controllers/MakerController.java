package dashboard.java.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import dashboard.java.Map;
import dashboard.java.actions.AddNodeAction;
import dashboard.java.actions.ClearAction;
import dashboard.java.actions.DeleteNodeAction;
import dashboard.java.actions.MoveNodeAction;
import dashboard.java.actions.RotateNodeAction;
import dashboard.java.floor.Floor;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class MakerController
{
	@FXML private TabPane tabPane;
	@FXML private ArrayList<Button> opList, gadgetList;
	@FXML private Button backBtn, clearBtn;
	@FXML private RadioButton dragRadio, rotateRadio, drawRadio, eraseRadio, deleteRadio;
	@FXML private ColorPicker colorPicker;
	@FXML private Slider lineWidthSlider;
	@FXML private MenuItem exportBtn, undoBtn, redoBtn;
	@FXML private TextField opSearchTxtFld, gadgetSearchTxtFld;
	
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/Maker.fxml"));

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
		
		for (Button button : opList)
		{
			button.setOnAction(e -> addNode(e));
			ImageView imgView = new ImageView(new Image("/dashboard/resources/Nodes/Ops/Def/" + button.getText() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/dashboard/css/Maker.css");
		}
		
		for (Button button : gadgetList)
		{
			button.setOnAction(e -> addNode(e));
			ImageView imgView = new ImageView(new Image("/dashboard/resources/Nodes/Gadgets/Def/" + button.getText() + ".png"));
			imgView.setFitHeight(56);
			imgView.setFitWidth(60);
			button.setGraphic(imgView);
			button.getStylesheets().add("/dashboard/css/Maker.css");
		}
		
		opSearchTxtFld.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		    for (Button button : opList)
		    {
		    	
		    	button.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(button.getText()).find());
		    	button.setManaged(button.isVisible());
		    }
		});
		
		gadgetSearchTxtFld.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		    for (Button button : gadgetList)
		    {
		    	
		    	button.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(button.getText()).find());
		    	button.setManaged(button.isVisible());
		    }
		});
		
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

		exportBtn.setOnAction(e -> 
		{
			DirectoryChooser dirChooser = new DirectoryChooser();
			File dir = dirChooser.showDialog(Global.primaryStage);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Saving... please wait");
			alert.show();
			dir = new File(dir.getAbsolutePath() + "/" + selectedMap);
			dir.mkdir();
			File files[] = new File[floors.length];
			try
			{
				for (int i = 0; i < files.length; i++)
				{
					WritableImage image = ((AnchorPane) ((ScrollPane) tabPane.getTabs().get(i).getContent()).getContent()).snapshot(new SnapshotParameters(), null);
					files[i] = new File(dir.getAbsolutePath() + "/" + selectedMap + " " + floors[i] + ".png");
					ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", files[i]);
				}
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			alert.hide();
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
			ScrollPane scrollPane = new ScrollPane();
			String floor = tabPane.getTabs().get(i).getText();
			Image img = new Image("/dashboard/resources/Blueprints/" + this.selectedMap.toString() + "/" + floor + ".jpg");
			
			floors[i] = new Floor(img, floor);
			
			AnchorPane anchorPane = new AnchorPane();
			anchorPane.getChildren().add(floors[i].getImgView());
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
				
				rotateNodeAction.setNewTheta((theta + Math.PI) * 180 / Math.PI);
				rotateNodeAction.execute();
				
				System.out.println(deltaX + "  " + deltaY + "  " + theta * 180 / Math.PI);
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
