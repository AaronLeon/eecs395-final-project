import java.util.HashMap;
import java.util.LinkedList;

public class Board {
    Pair<Pawn, Pawn>[] ring = new Pair[17 * 4];
    HashMap<String, Integer> homeLocations = new HashMap<>();
    HashMap<String, Integer> runwayLocations = new HashMap<>();
    HashMap<String, Home> homes = new HashMap<>();
    HashMap<String, Runway> runways = new HashMap<>();
    private int safeLocations[] = {0, 5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63};

    public boolean isBlockade(int pos) {
        return ring[pos].first != null && ring[pos].second != null;
    }

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

    public boolean remove(int location, String color, int id) {
        if (ring[location].first.color == color && ring[location].first.id == id) {
            ring[location].first = null;
            return true;
        } else if (ring[location].second.color == color && ring[location].second.id == id) {
            ring[location].second = null;
            return true;
        }
        return false;

    }

    public int bopLoc(int location, String color) {
        //assume no blockade
        boolean clear1 = ring[location].first == null;
        boolean clear2 = ring[location].second == null;

        if (clear1 && ring[location].second.color != color) {
            return 1;
        }
        if (clear2 && ring[location].first.color != color) {
            return 0;
        }
        return -1;
        //checks if inserting color at loc will bop
    }

    public void removeIndex(int location, int index) {
        if (index == 0) {
            ring[location].first = null;
        } else if (index == 1) {
            ring[location].second = null;
        }
    }

    public boolean add(int location, String color, int id) {
        //bops if needed
        boolean clear1 = ring[location].first == null;
        boolean clear2 = ring[location].second == null;

        if (clear1 && clear2) {
            ring[location].first = new Pawn(id, color);
        } else if (!clear1) {
            ring[location].second = new Pawn(id, color);
        } else {
            ring[location].first = new Pawn(id, color);
        }

        return false;
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
        private Pawn[][] runway = new Pawn[7][2];

        public Runway(String color) {
            this.color = color;
            pieces = 0;
        }

        public boolean empty(int i) {
            return runway[i][0] == null && runway[i][1] == null;
        }

        public boolean blocked(int i) {
            return runway[i][0] != null && runway[i][1] != null;
        }
    }
}