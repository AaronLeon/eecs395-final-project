package client;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import parcheesi.Blockade;
import parcheesi.Pawn;

public class UISquare extends FlowPane {
    public UISquare(int size, Color color, Object contents) {
        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setRowValignment(VPos.CENTER);
        this.setColumnHalignment(HPos.CENTER);
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        if (contents instanceof Pawn) {
            this.getChildren().add(new UIPawn(UIBoard.stringToColor(((Pawn) contents).color)));
        }
        else if (contents instanceof Blockade) {
            this.getChildren().add(new UIPawn(UIBoard.stringToColor(((Blockade) contents).first.color)));
            this.getChildren().add(new UIPawn(UIBoard.stringToColor(((Blockade) contents).second.color)));

        }
        else if (contents instanceof Object[]) {
            for (Object o : (Object[]) contents) {
                if (o != null && o instanceof Pawn) {
                    this.getChildren().add(new UIPawn(UIBoard.stringToColor(((Pawn) o).color)));
                }
            }
        }
    }
}
