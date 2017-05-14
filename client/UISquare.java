package client;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UISquare extends Rectangle {
    public UISquare(int size, Color color) {
        super(size, size);
        this.setStroke(Color.BLACK);
        this.setFill(color);
    }
}
