package dashboard.java.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import dashboard.java.Map;
import dashboard.java.actions.AddNodeAction;
import dashboard.java.actions.ClearAction;
import dashboard.java.actions.MoveNodeAction;
import dashboard.java.actions.RotateNodeAction;
import dashboard.java.gestures.NodeGestures;
import dashboard.java.gestures.SceneGestures;
import dashboard.java.global.Global;
import dashboard.java.undo.UndoCollector;
import dashboard.java.zoompane.ZoomPane;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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

	private NodeGestures nodeGestures;
	private SceneGestures sceneGestures;
	
	private AddNodeAction addNodeAction;
	private MoveNodeAction moveNodeAction;
	private RotateNodeAction rotateNodeAction;
	private ClearAction clearAction;
	
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
				for (Tab tab : tabPane.getTabs())
					((ZoomPane) tab.getContent()).getGC().setLineWidth(lineWidthSlider.getValue());
			}
		});
		
		moveNodeAction = new MoveNodeAction();
		addNodeAction = new AddNodeAction();
		rotateNodeAction = new RotateNodeAction();
		clearAction = new ClearAction();
		
		sceneGestures = new SceneGestures();
		nodeGestures = new NodeGestures();

		drawInit();
		addFloors();
		addSceneGestures();
		
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
		
		opSearchTxtFld.textProperty().addListener((observable, oldValue, newValue) -> {
		    for (Button button : opList)
		    {
		    	
		    	button.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(button.getText()).find());
		    	button.setManaged(button.isVisible());
		    }
		});
		
		gadgetSearchTxtFld.textProperty().addListener((observable, oldValue, newValue) -> {
		    for (Button button : gadgetList)
		    {
		    	
		    	button.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(button.getText()).find());
		    	button.setManaged(button.isVisible());
		    }
		});
		
		backBtn.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to go back?");
			alert.setHeaderText(null);
			Optional<ButtonType> result = alert.showAndWait();
			
			if (result.get() == ButtonType.OK)
				Global.selectionController.changeToScene();
		});
		
		clearBtn.setOnAction(e -> {
			clearAction.setOldPane(getCurrentPane());
			clearAction.execute();
			UndoCollector.INSTANCE.add(clearAction);
			clearAction.reset();
		});

		exportBtn.setOnAction(e -> {
			DirectoryChooser dirChooser = new DirectoryChooser();
			File dir = dirChooser.showDialog(Global.primaryStage);
			
			Alert alert = new Alert(AlertType.INFORMATION, "Saving... please wait");
			alert.setTitle("Saving...");
			alert.setHeaderText(null);
			alert.show();
			
			File folder = new File(dir.getAbsolutePath() + "/" + selectedMap);
			int count = 1;
			while (folder.exists())
			{
				folder = new File(dir.getAbsolutePath() + "/" + selectedMap + " (" + count + ")");
				count++;
			}

			folder.mkdir();
			
			try
			{
				for (Tab tab : tabPane.getTabs())
				{
					WritableImage image = ((ZoomPane) tab.getContent()).snapshot(new SnapshotParameters(), null);
					File file = new File(folder.getAbsolutePath() + "/" + selectedMap + " " + tab.getText() + ".png");
					ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
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
		
		Global.primaryStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
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
		
		for (int i = 0; i < tabPane.getTabs().size(); i++)
		{
			String floor = tabPane.getTabs().get(i).getText();
			Image img = new Image("/dashboard/resources/Blueprints/" + selectedMap + "/" + floor + ".jpg");
			
			ZoomPane zoomPane = new ZoomPane(new ImageView(img));
			
			tabPane.getTabs().get(i).setContent(zoomPane);
		}
	}
	
	private void addSceneGestures()
	{
		tabPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		tabPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		tabPane.addEventFilter(MouseEvent.MOUSE_RELEASED, sceneGestures.getOnMouseReleasedEventHandler());
		tabPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
	}
	
	public void addNode(ActionEvent event)
	{
		Image img = ((ImageView) ((Button) event.getSource()).getGraphic()).getImage();
		ImageView imgView = new ImageView(img);
		double imgRatio = img.getWidth() / img.getHeight();

		imgView.setFitHeight(75);
		imgView.setFitWidth(imgView.getFitHeight() * imgRatio);
		
		imgView.setTranslateX(Global.primaryStage.getScene().getWidth() / 2);
		imgView.setTranslateY(Global.primaryStage.getScene().getHeight() / 2);
		
		imgView.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
		imgView.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
		imgView.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
		imgView.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());
			
		addNodeAction.setImageView(imgView);
		if (addNodeAction.canExecute())
		{
			addNodeAction.execute();

			UndoCollector.INSTANCE.add(addNodeAction);
			addNodeAction.reset();
		}
	}
	
//	private void onImagePressed(MouseEvent event)
//	{
//		if (!deleteRadio.isSelected())
//		{			
//			rotateNodeAction.setImageView(imgView);
//			rotateNodeAction.setOldTheta(imgView.getRotate());
//		}
//	}
//	
//	private void onImageDragged(MouseEvent event)
//	{
//		if (event.getButton() == MouseButton.PRIMARY)
//		{
//			double mouseX = event.getSceneX();
//			double mouseY = event.getSceneY();
//			if (rotateRadio.isSelected())
//			{
//				double deltaX = event.getSceneX() - initX;
//				double deltaY = event.getSceneY() - initY;
//				double theta = Math.atan(deltaY/deltaX) + Math.PI / 2;
//				
//				if (deltaX == 0 && deltaY == 0)
//				{
//					theta = 0;
//				}
//				else if (deltaX >= 0)
//				{
//					theta += Math.PI;
//				}
//				
//				rotateNodeAction.setNewTheta((theta + Math.PI) * 180 / Math.PI);
//				rotateNodeAction.execute();
//				
//				System.out.println(deltaX + "  " + deltaY + "  " + theta * 180 / Math.PI);
//			}
//			else if (dragRadio.isSelected() && moveNodeAction.canExecute())
//			{
//				moveNodeAction.setNewX(scrollXPosition(mouseX) - xOffset);
//				moveNodeAction.setNewY(scrollYPosition(mouseY) - yOffset - 112);
//				moveNodeAction.execute();
//				System.out.println(moveNodeAction.getNewX() + " " + moveNodeAction.getNewY());
//			}
//		}
//	}
//	
//	private void onImageReleased(MouseEvent event)
//	{
//		ImageView imgView = (ImageView) event.getSource();
//		if (event.getButton() == MouseButton.PRIMARY)
//		{
//			if (dragRadio.isSelected())
//			{
//				moveNodeAction.setNewX(imgView.getX());
//				moveNodeAction.setNewY(imgView.getY());
//				UndoCollector.INSTANCE.add(moveNodeAction);
//				
//				moveNodeAction.reset();
//			}
//			else if (rotateRadio.isSelected())
//			{
//				System.out.println(imgView.getRotate());
//				rotateNodeAction.setNewTheta(imgView.getRotate());
//				UndoCollector.INSTANCE.add(rotateNodeAction);
//				
//				rotateNodeAction.reset();
//			}
//		}
//	}
//
//	
	public void drawInit()
	{
		colorPicker.setValue(Color.BLACK);
		colorPicker.getCustomColors().addAll(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE);
		colorPicker.setOnAction(e -> tabPane.getTabs().forEach(tab -> ((ZoomPane) tab.getContent()).getGC().setStroke(colorPicker.getValue())));
	}
	
	public boolean isDrawSelected()
	{
		return drawRadio.isSelected();
	}
	
	public boolean isEraseSelected()
	{
		return eraseRadio.isSelected();
	}
	
	public boolean isDeleteSelected()
	{
		return deleteRadio.isSelected();
	}
	
	public ColorPicker getColorPicker()
	{
		return this.colorPicker;
	}
	
	public Slider getLineWidthSlider()
	{
		return this.lineWidthSlider;
	}
	
	public ZoomPane getCurrentPane()
	{
		return (ZoomPane) tabPane.getSelectionModel().getSelectedItem().getContent();
	}
	
	public void setCurrentPane(ZoomPane zoomPane)
	{
		tabPane.getSelectionModel().getSelectedItem().setContent(zoomPane);
	}
}
