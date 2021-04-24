package dashboard.java.controller;

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
import dashboard.java.ZoomPane;
import dashboard.java.action.AddNodeAction;
import dashboard.java.action.ClearAction;
import dashboard.java.action.ResizeAction;
import dashboard.java.gesture.NodeGestures;
import dashboard.java.gesture.SceneGestures;
import dashboard.java.global.Global;
import dashboard.java.model.Data;
import dashboard.java.model.NodeData;
import dashboard.java.model.RandomString;
import dashboard.java.node.ImageNode;
import dashboard.java.undo.UndoCollector;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.ContextMenu;
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
import javafx.stage.FileChooser;

public class MakerController
{
	@FXML private BorderPane borderPane;
	@FXML private TabPane tabPane;
	@FXML private Button testBtn, backBtn, clearBtn;
	@FXML private RadioButton selectRadio, dragRadio, rotateRadio, drawRadio, eraseRadio, deleteRadio;
	@FXML private ColorPicker colorPicker;
	@FXML private Slider lineWidthSlider, nodeSizeSlider;
	@FXML private MenuItem exportBtn, saveBtn, saveAsBtn, openBtn, settingsBtn, undoBtn, redoBtn;
	@FXML private TextField opSearchTxtFld, gadgetSearchTxtFld;
	@FXML private ImageView selectedImgView;
	
	private Map selectedMap;

	private NodeGestures nodeGestures;
	private SceneGestures sceneGestures;
	
	private AddNodeAction addNodeAction;
	private ClearAction clearAction;
	private ResizeAction resizeAction;
	
	private Node selectedNode;
	
	public void show(Map selectedMap)
	{
		this.selectedMap = selectedMap;
		Global.data.setMap(selectedMap);

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
		addNodeAction = new AddNodeAction();
		clearAction = new ClearAction();
		resizeAction = new ResizeAction();
		
		sceneGestures = new SceneGestures();
		nodeGestures = new NodeGestures();
		
		lineWidthSlider.setValue(5.0);
		lineWidthSlider.valueProperty().addListener((v, oldVal, newVal) -> {
			for (Tab tab : tabPane.getTabs())
				((ZoomPane) tab.getContent()).getGC().setLineWidth(lineWidthSlider.getValue());
		});
		
		nodeSizeSlider.setOnMousePressed(e -> {
			if (selectedNode != null)
			{
				System.out.println(selectedNode);
				resizeAction.setNode(selectedNode);
				resizeAction.setOldScale(nodeSizeSlider.getValue());
			}
		});
		
		nodeSizeSlider.setOnMouseReleased(e -> {
			if (selectedNode != null)
			{

				resizeAction.setNewScale(nodeSizeSlider.getValue());
				resizeAction.execute();
				
				UndoCollector.INSTANCE.add(resizeAction);
				resizeAction.reset();
			}
		});
		
		nodeSizeSlider.valueProperty().addListener((v, oldVal, newVal) -> {
			if (selectedNode != null)
			{
				selectedNode.setScaleX(newVal.doubleValue());
				selectedNode.setScaleY(newVal.doubleValue());
			}
		});

		drawInit();
		addFloors();
		addSceneGestures();
		
		TabPane tp = ((TabPane) borderPane.getRight());
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
		
		initButtons();
		initShortcuts();
	}
	
	private void initButtons()
	{
		backBtn.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to go back?");
			alert.setHeaderText(null);
			Optional<ButtonType> result = alert.showAndWait();
			
			if (result.get() == ButtonType.OK)
				Global.selection.show();
		});
		
		clearBtn.setOnAction(e -> {
			clearAction.setOldPane(getCurrentPane());
			clearAction.execute();
			UndoCollector.INSTANCE.add(clearAction);
			clearAction.reset();
		});
		
		testBtn.setOnAction(e -> {
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
		
		saveAsBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON File (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showSaveDialog(Global.primaryStage);
			Data.serialize(file);
		});
		
		openBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(Global.primaryStage);
			Data.deserialize(file);
		});
		
		settingsBtn.setOnAction(e -> {
			Global.settings.show();
		});
		
		undoBtn.setOnAction(e -> {
			UndoCollector.INSTANCE.undo();
//			if (UndoCollector.INSTANCE.getLastUndo() == null)
//				undoBtn.setDisable(true);
//			else
//				undoBtn.setDisable(false);
		});
		redoBtn.setOnAction(e -> {
			UndoCollector.INSTANCE.redo();
//			if (UndoCollector.INSTANCE.getLastRedo() == null)
//				redoBtn.setDisable(true);
//			else
//				redoBtn.setDisable(false);
		});
	}
	
	private void initShortcuts()
	{
		Global.primaryStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.DIGIT1)
				dragRadio.fire();
			if (key.getCode() == KeyCode.DIGIT2)
				rotateRadio.fire();
			if (key.getCode() == KeyCode.DIGIT3)
				drawRadio.fire();
			if (key.getCode() == KeyCode.DIGIT4)
				eraseRadio.fire();
			if (key.getCode() == KeyCode.DIGIT5)
				deleteRadio.fire();
			
			if (key.isControlDown())
			{
				if (key.getCode() == KeyCode.Z)
					undoBtn.fire();
				
				if (key.getCode() == KeyCode.Y)
					redoBtn.fire();
			}
		});
	}
	
	private void fillTab(Tab tab, JarFile jar, String path)
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
		search.textProperty().addListener((observable, oldValue, newValue) -> {
		    for (Node n : vbIn.getChildren())
		    {
		    	n.setVisible(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(((Button) n).getText()).find());
		    	n.setManaged(n.isVisible());
		    }
		});

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements())
		{
			String filePath = entries.nextElement().getName();
			if (filePath.startsWith(path + "/") && filePath.contains("."))
			{
				String name = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.'));
				name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
				Button button = new Button(name);
				button.setOnAction(e -> addNode(filePath));
				button.setTextFill(Color.WHITE);
				button.setPrefWidth(200);
				button.setPrefHeight(64);
				button.setAlignment(Pos.CENTER_LEFT);
				button.setStyle("-fx-font-size:16");
				
				ImageView imgView = new ImageView("/" + filePath);
				imgView.setFitHeight(75);
				imgView.setFitWidth(75);
				button.setGraphic(imgView);
				
				button.getStylesheets().add("/dashboard/css/Maker.css");
				vbIn.getChildren().add(button);
			}
		}
	}
	
	private void fillTab(Tab tab, URL url, String path)
	{
		TabPane tp = ((TabPane) borderPane.getRight());
		ScrollPane sp = new ScrollPane();
		VBox vbIn = new VBox(10);
		VBox vbOut = new VBox(10);
		TextField search = new TextField();
		
		vbIn.setPrefWidth(tp.getWidth());
		vbIn.setSpacing(10);
		vbIn.setPadding(new Insets(10));
		vbIn.setStyle("-fx-background-color: #3f3f3f");
		
		vbOut.setAlignment(Pos.TOP_CENTER);
		vbOut.setPadding(new Insets(5, 0, 0, 0));
		vbOut.setStyle("-fx-background-color: #3f3f3f");
		vbOut.setAlignment(Pos.TOP_CENTER);

		sp.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setContent(vbIn);
		vbOut.getChildren().addAll(search, sp);
		tab.setContent(vbOut);
		
		search.setMaxWidth(200);
		search.setPromptText("Search");
		search.textProperty().addListener((observable, oldValue, newValue) -> {
		    for (Node n : vbIn.getChildren())
		    {
		    	n.setManaged(Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(((Button) n).getText()).find());
		    }
		});

		if (url != null)
		{
			try
			{
				File dir = new File(url.toURI());
				for (File file : dir.listFiles())
				{
					String name = Character.toUpperCase(file.getName().charAt(0)) + file.getName().substring(1, file.getName().lastIndexOf('.'));
					Button button = new Button(name);
					button.setOnAction(e -> addNode(path + "/" + file.getName()));
					button.setTextFill(Color.WHITE);
					button.setPrefWidth(200);
					button.setPrefHeight(64);
					button.setAlignment(Pos.CENTER_LEFT);
					button.setStyle("-fx-font-size:16");
					
					ImageView imgView = new ImageView("/" + path + "/" + file.getName());
					imgView.setFitHeight(75);
					imgView.setFitWidth(75);
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
		switch (selectedMap)
		{
		case CHALET :
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
			break;
		case CLUBHOUSE :
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
			break;
		case COASTLINE :
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"));
			break;
		case CONSULATE :
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
			break;
		case KAFE :
			tabPane.getTabs().addAll(new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("3rd Floor"));
			break;
		case OREGON :
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"), new Tab("T3"));
			break;
		case VILLA :
			tabPane.getTabs().addAll(new Tab("Basement"), new Tab("1st Floor"), new Tab("2nd Floor"));
			break;
		}
		
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
		tabPane.addEventFilter(MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
		tabPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
	}
	
	public void addNode(String path)
	{
		ImageNode imgNode = addImageNode(path, getCurrentPane().getWidth() / 2, getCurrentPane().getHeight() / 2, new RandomString().nextString(), 0);

		addNodeAction.setImageNode(imgNode);
		if (addNodeAction.canExecute())
		{
			addNodeAction.execute();
			Global.data.addNode(imgNode, path, getCurrentTab().getText());
			
			UndoCollector.INSTANCE.add(addNodeAction);
			addNodeAction.reset();
		}
	}
	
	public void addNodeJSON(NodeData nodeData)
	{
		ImageNode imgNode = addImageNode(nodeData.getPath(), nodeData.getX(), nodeData.getY(), nodeData.getId(), nodeData.getAngle());
		
		Tab currentTab = null;
		for (Tab tab : Global.maker.getTabPane().getTabs())
		{
			if (tab.getText().equals(nodeData.getTab()))
			{
				currentTab = tab;
				break;
			}
		}
		((ZoomPane) currentTab.getContent()).getChildren().add(imgNode);
	}
	
	private ImageNode addImageNode(String url, double x, double y, String id, double angle)
	{
		Image img = new Image(url);
		ImageNode imgNode = new ImageNode(img);

		double imgRatio = img.getWidth() / img.getHeight();

		imgNode.setFitHeight(75);
		imgNode.setFitWidth(imgNode.getFitHeight() * imgRatio);
		imgNode.setPickOnBounds(true);
		imgNode.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
		imgNode.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
		imgNode.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
		imgNode.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());
		imgNode.setTranslateX(x);
		imgNode.setTranslateY(y);
		imgNode.setId(id);
		imgNode.setRotate(angle);
		
		ColorPicker cp = new ColorPicker();
		cp.setValue(Color.BLACK);
		cp.getCustomColors().addAll(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.MAGENTA);
	    MenuItem menu = new MenuItem(null, cp);
	    menu.setOnAction(e -> imgNode.getBorder().setStroke(cp.getValue()));

	    ContextMenu contextMenu = new ContextMenu(menu);
	    imgNode.setOnContextMenuRequested(e -> contextMenu.show(imgNode, e.getScreenX(), e.getScreenY()));
		
		return imgNode;
	}
	
	public void drawInit()
	{
		colorPicker.setValue(Color.BLACK);
		colorPicker.getCustomColors().addAll(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.MAGENTA);
		colorPicker.setOnAction(e -> tabPane.getTabs().forEach(tab -> ((ZoomPane) tab.getContent()).getGC().setStroke(colorPicker.getValue())));
	}
	
	public boolean isDragSelected()
	{
		return dragRadio.isSelected();
	}
	
	public boolean isRotateSelected()
	{
		return rotateRadio.isSelected();
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
	
	public void setSelectedNode(Node selectedNode)
	{
		this.selectedNode = selectedNode;
		if (selectedNode == null)
		{
			nodeSizeSlider.setValue(1);
			selectedImgView.setImage(null);
		}
		else
		{
			nodeSizeSlider.setValue(selectedNode.getScaleX());
			
			if (selectedNode instanceof ImageNode)
				selectedImgView.setImage(((ImageNode) selectedNode).getImage());
		}
	}
	
	public ColorPicker getColorPicker()
	{
		return colorPicker;
	}
	
	public Slider getLineWidthSlider()
	{
		return lineWidthSlider;
	}
	
	public Map getMap()
	{
		return selectedMap;
	}
	
	public ZoomPane getCurrentPane()
	{
		return (ZoomPane) getCurrentTab().getContent();
	}
	
	public void setCurrentPane(ZoomPane zoomPane)
	{
		getCurrentTab().setContent(zoomPane);
	}
	
	public Tab getCurrentTab()
	{
		return tabPane.getSelectionModel().getSelectedItem();
	}
	
	public TabPane getTabPane()
	{
		return tabPane;
	}
}
