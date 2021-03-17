package dashboard.java.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import dashboard.java.Map;
import dashboard.java.actions.AddNodeAction;
import dashboard.java.actions.ClearAction;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class MakerController
{
	@FXML private BorderPane borderPane;
	@FXML private TabPane tabPane;
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
		
		addNodeAction = new AddNodeAction();
		clearAction = new ClearAction();
		
		sceneGestures = new SceneGestures();
		nodeGestures = new NodeGestures();

		drawInit();
		addFloors();
		addSceneGestures();
		
		TabPane tp = ((TabPane) borderPane.getRight());////////////////////////////////////////////////////////////////////////////////////////////////
		for (Tab tabOut : tp.getTabs())
		{
			for (Tab tabIn : ((TabPane) tabOut.getContent()).getTabs())
			{
				final String path = "dashboard/resources/Nodes/" + tabOut.getText() + "/" + tabIn.getText();
				final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

				if (jarFile.isFile())
				{
					try
					{
						JarFile jar = new JarFile(jarFile);
						fillTab(tabIn, jar, path);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					final URL url = getClass().getResource("/" + path);
					fillTab(tabIn, url, path);
				}
			}
		}
		
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
	
	public void fillTab(Tab tab, JarFile jar, String path)
	{
		TabPane tp = ((TabPane) borderPane.getRight());
		ScrollPane sp = new ScrollPane();
		VBox vbIn = new VBox(10);
		VBox vbOut = new VBox(10);
		TextField search = new TextField();
		
		vbOut.setAlignment(Pos.TOP_CENTER);
		
		vbIn.setPrefWidth(tp.getWidth());
		vbIn.setSpacing(10);
		vbIn.setPadding(new Insets(10));
		vbIn.setStyle("-fx-background-color: #3f3f3f");
		
		vbOut.setAlignment(Pos.TOP_CENTER);
		vbOut.setPadding(new Insets(5, 0, 0, 0));
		vbOut.setStyle("-fx-background-color: #3f3f3f");
		
		sp.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setContent(vbIn);
		vbOut.getChildren().addAll(search, sp);
		tab.setContent(vbOut);
		
		search.setMaxWidth(200);
		search.setPromptText("Search");
		search.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		    for (Node n : vbIn.getChildren())
		    {
		    	n.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(((Button) n).getText()).find());
		    	n.setManaged(n.isVisible());
		    }
		});

		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements())
		{
			String filePath = entries.nextElement().getName();
			if (filePath.startsWith(path + "/") && filePath.contains("."))
			{
				String name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length() - 4);
				Button button = new Button(name);
				button.setOnAction(e -> addNode(e));
				button.setTextFill(Color.WHITE);
				button.setPrefWidth(180);
				button.setPrefHeight(64);
				button.setAlignment(Pos.CENTER_LEFT);
				button.setStyle("-fx-font-size:16");
				
				ImageView imgView = new ImageView("/" + filePath);
				imgView.setFitHeight(56);
				imgView.setFitWidth(60);
				button.setGraphic(imgView);
				
				button.getStylesheets().add("/dashboard/css/Maker.css");
				vbIn.getChildren().add(button);
			}
		}
	}
	
	public void fillTab(Tab tab, URL url, String path)
	{
		TabPane tp = ((TabPane) borderPane.getRight());
		ScrollPane sp = new ScrollPane();
		VBox vbIn = new VBox(10);
		VBox vbOut = new VBox(10);
		TextField search = new TextField();
		
		vbOut.setAlignment(Pos.TOP_CENTER);
		
		vbIn.setPrefWidth(tp.getWidth());
		vbIn.setSpacing(10);
		vbIn.setPadding(new Insets(10));
		vbIn.setStyle("-fx-background-color: #3f3f3f");
		
		vbOut.setAlignment(Pos.TOP_CENTER);
		vbOut.setPadding(new Insets(5, 0, 0, 0));
		vbOut.setStyle("-fx-background-color: #3f3f3f");
		
		sp.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setContent(vbIn);
		vbOut.getChildren().addAll(search, sp);
		tab.setContent(vbOut);
		
		search.setMaxWidth(200);
		search.setPromptText("Search");
		search.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		    for (Node n : vbIn.getChildren())
		    {
		    	n.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(((Button) n).getText()).find());
		    	n.setManaged(n.isVisible());
		    }
		});

		if (url != null)
		{
			try
			{
				File dir = new File(url.toURI());
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
					
					ImageView imgView = new ImageView("/" + path + "/" + file.getName());
					imgView.setFitHeight(56);
					imgView.setFitWidth(60);
					button.setGraphic(imgView);
					
					button.getStylesheets().add("/dashboard/css/Maker.css");
					vbIn.getChildren().add(button);
				}
			}
			catch (URISyntaxException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	private void addFloors()
	{
		if (selectedMap == Map.CHALET)
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
		else if (selectedMap == Map.CLUBHOUSE)
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
		imgView.setPickOnBounds(true);
		
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
	
	public boolean isDragSelected()
	{
		return dragRadio.isSelected();
	}
	
	public boolean isRotateSelected()
	{
		return rotateRadio.isSelected();
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
