package client;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import parcheesi.Board;

public class UIBoard extends GridPane {
    final int WIDTH = 10;
    final int HEIGHT = 10;
    Board board;

    public UIBoard(Board board) {
        this.board = board;
        this.setHeight(400);
        this.setWidth(400);

        this.setGridLinesVisible(true);

        RowConstraints rc = new RowConstraints(40);
        rc.setFillHeight(true);
        ColumnConstraints cc = new ColumnConstraints(40);
        cc.setFillWidth(true);
        this.getRowConstraints().add(rc);
        this.getColumnConstraints().add(cc);


        for (int row = 0; row < HEIGHT; ++row) {
            for (int col = 0; col < WIDTH; ++col) {
                this.add(new UISquare(40), col, row);
            }
        }


    }



}
