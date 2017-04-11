import java.util.HashMap;
import java.util.LinkedList;

public class Board {
    public static Pawn[][] ring = new Pawn[17*4][2];
    private HashMap homeLocations = new HashMap<String, Integer>();
    private HashMap runwayLocations = new HashMap<String, Integer>();
    HashMap<String, Home> homes = new HashMap();
    HashMap<String, Runway> runways = new HashMap();
    private int safeLocations[] = {0, 5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63};

    public Board() {
        homeLocations.put("blue", 5);
        homeLocations.put("yellow", 22);
        homeLocations.put("green", 39);
        homeLocations.put("red", 56);

        runwayLocations.put("blue", 0);
        runwayLocations.put("yellow", 17);
        runwayLocations.put("green", 34);
        runwayLocations.put("red", 51);

        runways.put("blue", new Runway("blue"));
        runways.put("yellow", new Runway("yellow"));
        runways.put("green", new Runway("green"));
        runways.put("red", new Runway("red"));

        homes.put("blue", new Home("blue"));
        homes.put("yellow", new Home("yellow"));
        homes.put("green", new Home("green"));
        homes.put("red", new Home("red"));
    }

    public class Home {
        private String color;
        private LinkedList<Pawn> home;

        public Home(String color) {
           this.color = color;

           home = new LinkedList<>();
           for (int i = 0; i < 4; i++) {
               home.add(new Pawn(i, color));
           }
        }
    }

    public class Runway {
        private String color;
        private int pieces;
        private Pawn[] runway = new Pawn[7];

        public Runway(String color) {
            this.color = color;
            pieces = 0;
        }
    }
}
