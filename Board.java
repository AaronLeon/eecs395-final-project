import java.util.HashMap;

public class Board {
    private Pawn[] ring = new Pawn[17*4];
    private HashMap<String, Integer> homeLocations = new HashMap();
    private HashMap<String, Integer> runwayLocations = new HashMap();
    private HashMap<String, Home> homes = new HashMap();
    private HashMap<String, Runway> runways = new HashMap();
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
        private int pieces;

        public Home(String color) {
           this.color = color;
           pieces = 4;
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
