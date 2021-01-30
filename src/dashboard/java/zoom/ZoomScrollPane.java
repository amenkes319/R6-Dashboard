package dashboard.java.zoom;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class ZoomScrollPane extends ScrollPane 
{
	public ZoomScrollPane(AnchorPane pane)
	{
		super();
		this.setContent(pane);
	}
//    private double scaleValue = 0.7;
//    private double zoomIntensity = 0.02;
//    private Node target;
//    private Node zoomNode;
//
//    public ZoomScrollPane(Node target) 
//    {
//        super();
//        this.target = target;
//        this.zoomNode = new Group(target);
//        setContent(content(target));
//
//        setPannable(true);
//        setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        setFitToHeight(true);
//        setFitToWidth(true);
//
//        updateScale();
//    }
//
//    private Node content(Node node) 
//    {
//    	node.setOnScroll(e ->
//        {
//        	if (e.isControlDown())
//        	{
//            	e.consume();
//            	onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
//        	}
//        });
//        
//    	node.addEventHandler(MouseEvent.ANY, e ->
//        {
//        	if (e.getButton() != MouseButton.MIDDLE)
//        		e.consume();
//        });
//        return node;
//    }
//    
//    private void updateScale() 
//    {
//        target.setScaleX(scaleValue);
//        target.setScaleY(scaleValue);
//        
//        for (Node n : ((AnchorPane) target).getChildren())
//        {
//        	n.setScaleX(scaleValue);
//        	n.setScaleY(scaleValue);
//        }
//    }
//
//    private void onScroll(double wheelDelta, Point2D mousePoint) 
//    {
//        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);
//
//        Bounds innerBounds = zoomNode.getLayoutBounds();
//        Bounds viewportBounds = getViewportBounds();
//
//        // calculate pixel offsets from [0, 1] range
//        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
//        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());
//
//        scaleValue *= zoomFactor;
//        updateScale();
//        this.layout(); // refresh ScrollPane scroll positions & target bounds
//
//        // convert target coordinates to zoomTarget coordinates
//        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));
//
//        // calculate adjustment of scroll position (pixels)
//        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));
//
//        // convert back to [0, 1] range
//        // (too large/small values are automatically corrected by ScrollPane)
//        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
//        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
//        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
//    }
}