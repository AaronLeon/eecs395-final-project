package client;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class UIPawn extends Circle {
    public UIPawn(Color color) {
        super(5);
        this.setStroke(Color.BLACK);
        this.setFill(color);
    }
}
