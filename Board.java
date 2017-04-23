import java.util.HashMap;

public class Board {
    Pair<Pawn, Pawn>[] ring = new Pair[17 * 4];
    HashMap<String, Integer> homeLocations = new HashMap<>();
    HashMap<String, Integer> runwayLocations = new HashMap<>();
    HashMap<String, Runway> runways = new HashMap<>();
    private int safeLocations[] = {0, 5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63};

    public boolean isBlockade(int pos) {
        return ring[pos].first != null && ring[pos].second != null;
    }

    public boolean invariant(int pos){
        if (ring[pos].first!=null && ring[pos].second!=null){
            return ring[pos].first.color==ring[pos].second.color;
        }
        return true;
    }

    public Board() {
        for (int i=0;i<17*4;i++){
            ring[i]=new Pair();
        }
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

    }

    public boolean blocked(int i) {
        return ring[i].first != null && ring[i].second != null;
    }

    public Pair<Pawn, Pawn> clearCell(int i){
        Pair<Pawn, Pawn>set = new Pair();
        set.first=ring[i].first;
        set.second=ring[i].second;
        ring[i]=new Pair();
        if(invariant(i)==false){
            return null;
        }
        return set;
    }


    public boolean remove(int location, String color, int id) {
        if (ring[location].first.color == color && ring[location].first.id == id) {
            ring[location].first = null;
            return true;
        } else if (ring[location].second.color == color && ring[location].second.id == id) {
            ring[location].second = null;
            return true;
        }

        if(invariant(location)==false){
            return false;
        }

        return false;

    }

    public int bopLoc(int location, String color) {
        //assume no blockade
        boolean clear1 = ring[location].first == null;
        boolean clear2 = ring[location].second == null;

        if(clear1&&clear2){
            return -1;
        }

        if (clear1 && ring[location].second.color != color) {
            return 1;
        }
        if (clear2 && ring[location].first.color != color) {
            return 0;
        }

        if(invariant(location)==false){
            return -1;
        }

        return -1;
        //checks if inserting color at loc will bop
    }

    public Pawn bopTarget(int location, int index) {
        for(int loc: safeLocations){
            if(loc==location){
                return null;
            }
        }
        if(invariant(location)==false){
            return null;
        }

        Pawn copy = new Pawn(-1, null);
        if (index == 0) {
            copy.color = ring[location].first.color;
            ring[location].first = null;
        } else if (index == 1) {
            copy.color = ring[location].second.color;
            ring[location].second = null;
        }
        return copy;
    }

    public boolean add(int location, String color, int id) {
        //bops if needed
        boolean clear1 = ring[location].first == null;
        boolean clear2 = ring[location].second == null;
        if (clear1) {
            ring[location].first = new Pawn(id, color);
            ring[location].first.home=false;
            ring[location].first.location=location;
            return true;
        } else if (clear2) {
            ring[location].second = new Pawn(id, color);;
            ring[location].first.home=false;
            ring[location].first.location=location;
            return true;
        }

        if(invariant(location)==false){
            return false;
        }

        return false;
    }


    public class Runway {
        String color;
        int pieces;
        Pair<Pawn, Pawn>[] runway = new Pair[6];
        Pawn[] endZone = new Pawn[4];

        public Runway(String color) {
            this.color = color;
            pieces = 0;
        }



        public boolean remove(int location, String color, int id) {
            if (runway[location].first.color == color && runway[location].first.id == id) {
                runway[location].first = null;
                return true;
            } else if (runway[location].second.color == color && runway[location].second.id == id) {
                runway[location].second = null;
                return true;
            }
            return false;
        }

        public boolean add(int location, String color, int id) {
            if (location == 7) {
                for (int i = 0; i < 4; i++) {
                    if (endZone[i] == null) {
                        endZone[i] = new Pawn(id, color);
                    }
                }
                return true;
            }
            //add to endzone
            else {
                boolean clear1 = runway[location].first == null;
                boolean clear2 = runway[location].second == null;
                if (clear1) {
                    runway[location].first = new Pawn(id, color);
                    return true;
                } else if (clear2) {
                    runway[location].second = new Pawn(id, color);
                    return true;
                }
                return false;


            }
        }

        public boolean empty(int i) {
            return runway[i].isEmpty();
        }

        public boolean blocked(int i) {
            return runway[i].first != null && runway[i].second != null;
        }
    }
}
