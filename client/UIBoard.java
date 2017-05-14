package client;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import parcheesi.Board;
import parcheesi.Pair;

import java.util.HashMap;

public class UIBoard extends GridPane {
    final int COLS = 20;
    final int ROWS = 20;
    final int CELL_HEIGHT = 30;
    final int CELL_WIDTH = 30;
    Board board;

    public UIBoard(Board board) {
        this.board = board;
        this.setHeight(600);
        this.setWidth(600);
        this.setGridLinesVisible(true);

        RowConstraints rc = new RowConstraints(CELL_HEIGHT);
        rc.setFillHeight(true);
        ColumnConstraints cc = new ColumnConstraints(CELL_WIDTH);
        cc.setFillWidth(true);
        this.getRowConstraints().add(rc);
        this.getColumnConstraints().add(cc);

        Pair[] ringToGridIndex = new Pair[Board.RING_SIZE];
        HashMap<String, Pair[]> homeRowToGridIndex = new HashMap<>();
        {{
            homeRowToGridIndex.put("blue", new Pair[Board.HOMEROW_SIZE]);
            homeRowToGridIndex.put("green", new Pair[Board.HOMEROW_SIZE]);
            homeRowToGridIndex.put("yellow", new Pair[Board.HOMEROW_SIZE]);
            homeRowToGridIndex.put("red", new Pair[Board.HOMEROW_SIZE]);
        }}

        HashMap<String, Pair> homeToGridIndex = new HashMap<>();
        {{
            homeToGridIndex.put("blue", new Pair());
            homeToGridIndex.put("green", new Pair());
            homeToGridIndex.put("yellow", new Pair());
            homeToGridIndex.put("red", new Pair());
        }}

        HashMap<String, Pair> nestToGridIndex = new HashMap<>();
        {{
            homeToGridIndex.put("blue", new Pair());
            homeToGridIndex.put("green", new Pair());
            homeToGridIndex.put("yellow", new Pair());
            homeToGridIndex.put("red", new Pair());
        }}

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                this.add(new UISquare(30), col, row);
            }
        }
    }
}
