package client;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
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
//        this.setGridLinesVisible(true);

        RowConstraints rc = new RowConstraints(CELL_HEIGHT);
        rc.setFillHeight(true);
        ColumnConstraints cc = new ColumnConstraints(CELL_WIDTH);
        cc.setFillWidth(true);
        this.getRowConstraints().add(rc);
        this.getColumnConstraints().add(cc);

        Pair<Integer, Integer>[] ringToGridIndex = fillRingToGridIndex();

        HashMap<String, Pair<Integer, Integer>[]> homeRowToGridIndex = new HashMap<>();
        {{
            Pair[] blueRunway = new Pair[Board.HOMEROW_SIZE];
            for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
                blueRunway[i] = new Pair<>(9, 1 + i);
            }
            homeRowToGridIndex.put("blue", blueRunway);

            Pair[] greenRunway = new Pair[Board.HOMEROW_SIZE];
            for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
                greenRunway[i] = new Pair<>(9, 11 + i);
            }
            homeRowToGridIndex.put("green", greenRunway);


            Pair[] yellowRunway = new Pair[Board.HOMEROW_SIZE];
            for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
                yellowRunway[i] = new Pair<>(1 + i, 9);
            }

            homeRowToGridIndex.put("yellow", yellowRunway);
            Pair[] redRunway = new Pair[Board.HOMEROW_SIZE];
            for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
                redRunway[i] = new Pair<>(11 + i, 9);
            }
            homeRowToGridIndex.put("red", redRunway);
        }}

        HashMap<String, Pair<Integer, Integer>> homeToGridIndex = new HashMap<>();
        {{
            homeToGridIndex.put("blue", new Pair<>(9, 8));
            homeToGridIndex.put("green", new Pair<>(9, 10));
            homeToGridIndex.put("yellow", new Pair<>(8, 9));
            homeToGridIndex.put("red", new Pair<>(10, 9));
        }}

        HashMap<String, Pair<Integer, Integer>> nestToGridIndex = new HashMap<>();
        {{
            nestToGridIndex.put("blue", new Pair<>(7, 4));
            nestToGridIndex.put("green", new Pair<>(11, 14));
            nestToGridIndex.put("yellow", new Pair<>(4, 11));
            nestToGridIndex.put("red", new Pair<>(14, 7));
        }}

        for (int pos = 0; pos < Board.RING_SIZE; ++pos) {
            Pair<Integer, Integer> cellLocation = ringToGridIndex[pos];
            Color color = Board.isSafe(pos)
                    ? Color.MEDIUMPURPLE
                    : Color.BEIGE;
            this.add(new UISquare(30, color), cellLocation.first, cellLocation.second);
        }

        for (String c: Board.COLORS) {
            Color color = null;
            switch (c) {
                case "blue":
                    color = Color.BLUE;
                    break;
                case "green":
                    color = Color.GREEN;
                    break;
                case "yellow":
                    color = Color.YELLOW;
                    break;
                case "red":
                    color = Color.RED;
                    break;
                default:
                    break;
            }

            this.add(new UISquare(30, color), nestToGridIndex.get(c).first, nestToGridIndex.get(c).second);
            for (Pair<Integer, Integer> location: homeRowToGridIndex.get(c)) {
                this.add(new UISquare(30, color), location.first, location.second);
            }
            this.add(new UISquare(30, color), homeToGridIndex.get(c).first, homeToGridIndex.get(c).second);
        }
    }

    private Pair<Integer, Integer>[] fillRingToGridIndex() {
        Pair[] indexing = new Pair[Board.RING_SIZE];
        for (int i = 0; i <= 7; i++) {
            indexing[i] = new Pair<>(8, i);
        }
        for (int i = 8; i <= 15; i++) {
            indexing[i] = new Pair<>(15 - i, 8);
        }
        indexing[16] = new Pair<>(0, 9);
        for (int i = 17; i <= 27; i++) {
            indexing[i] = new Pair<>(i - 17, 10);
        }
        for (int i = 25; i <= 32; i++) {
            indexing[i] = new Pair<>(8, i - 14);
        }
        indexing[33] = new Pair<>(9, 18);
        for (int i = 34; i <= 41; i++) {
            indexing[i] = new Pair<>(10, 34 + 18 - i);
        }
        for (int i = 42; i <= 49; i++) {
            indexing[i] = new Pair<>(i - 31, 10);
        }
        indexing[50] = new Pair<>(18, 9);
        for (int i = 51; i <= 58; i++) {
            indexing[i] = new Pair<>(51 + 18 - i, 8);
        }
        for (int i = 59; i <= 66; i++) {
            indexing[i] = new Pair<>(10, 66 - i);
        }
        indexing[67] = new Pair<>(9, 0);
        return indexing;
    }
}
