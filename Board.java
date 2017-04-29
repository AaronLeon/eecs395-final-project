import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    public enum BoardComponent {
        NEST, RING, HOMEROW, HOME
    }

    static final String[] COLORS = {"blue", "yellow", "green", "red"};
    static final int RING_SIZE = 17 * 4;
    static final int HOMEROW_SIZE =  7;
    static final int NEST_SIZE = 4;
    static final int HOME_SIZE = 4;

    static final HashMap<String, Integer> NEST_LOCATIONS = new HashMap<String, Integer>() {
        {
            put("blue", 5);
            put("yellow", 22);
            put("green", 39);
            put("red", 56);
        }
    };
    static final HashMap<String, Integer> HOMEROW_LOCATIONS = new HashMap<String, Integer>() {
        {
            put("blue", 0);
            put("yellow", 17);
            put("green", 34);
            put("red", 51);
        }
    };
    static final int SAFE_LOCATIONS[] = {0, 5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63};

    Object[] ring;
    HashMap<String, ArrayList<Object>> nests;
    HashMap<String, Object[]> homeRows;
    HashMap<String, ArrayList<Object>> homes;
    HashMap<String, Pawn[]> pawns;

    public Board() {
        // Initialize Ring
        ring = new Object[RING_SIZE];

        // Initialize Nests
        for (String color: COLORS) {
            nests.put(color, new ArrayList<>(NEST_SIZE));
        }

        // Initialize Runways
        for (String color: COLORS) {
            homeRows.put(color, new Object[HOMEROW_SIZE]);
        }

        // Initialize Homes
        for (String color: COLORS) {
            homes.put(color, new ArrayList<>(NEST_SIZE));
        }
    }


    public boolean isBlockade(BoardComponent bc, int location, String color) {
        switch (bc) {
            case RING:
                return ring[location] instanceof Pair;
            case HOMEROW:
                return homes.get(color).get(location) instanceof Pair;
        }
        return false;
    }

    public void sendBackToNest(Pawn pawn) {
        if (pawn.bc == BoardComponent.RING) {
            ring[pawn.location] = null;
        }
        else if (pawn.bc == BoardComponent.HOMEROW) {
            homeRows.get(pawn.color)[pawn.location] = null;
        }
        else {
            return;
        }
        pawn.bc = BoardComponent.NEST;
        pawn.location = -1;
        nests.get(pawn.color)
                .add(pawn);
    }

    public void enterPiece(Pawn pawn) {
        if (pawn.bc != BoardComponent.NEST) {
            return;
        }
        int homeLocation = NEST_LOCATIONS.get(pawn.color);
        nests.get(pawn.color)
                .remove(pawn.location);
        pawn.bc = BoardComponent.RING;
        pawn.location = homeLocation;
        ring[homeLocation] = pawn;
    }

    public boolean movePawnRing(Pawn pawn, int distance) {
        int homeRowLocation = HOMEROW_LOCATIONS.get(pawn.color);
        int newLocation = pawn.location + distance;
        ring[pawn.location] = null;
        if (newLocation > homeRowLocation) {
            int newDistance = (newLocation % homeRowLocation) - 1;
            pawn.bc = BoardComponent.HOMEROW;
            pawn.location = 0;
            return movePawnHomeRow(pawn, newDistance);
        }
        pawn.location = newLocation;
        ring[newLocation] = pawn;
        return false;
    }

    public boolean movePawnHomeRow(Pawn pawn, int distance) {
        int newLocation = pawn.location + distance;
        if (newLocation > 6) {
            movePawnHome(pawn);
            return true;
        }
        else {
            homeRows.get(pawn.color)[pawn.location] = null;
            pawn.location = newLocation;
            homeRows.get(pawn.color)[newLocation] = pawn;
        }
        return false;
    }

    public void movePawnHome(Pawn pawn) {
        if (pawn.bc == BoardComponent.RING) {
            ring[pawn.location] = null;
        }
        else if (pawn.bc == BoardComponent.HOMEROW){
            homeRows.get(pawn.color)[pawn.location] = null;
        }
        pawn.bc = BoardComponent.HOME;
        pawn.location = -1;
        homes.get(pawn.color).add(pawn);
    }



    public void formBlockade(Pawn p1, Pawn p2) throws Exception {
        if (!p1.color.equals(p2)) {
            throw new Exception("Forming invalid blockade!");
        }

    }

    public Object get(BoardComponent bc, int location, String color) {
        switch(bc) {
            case RING:
                return ring[location];
            case NEST:
                return nests.get(color).get(location);
            case HOMEROW:
                return homeRows.get(color)[(location)];
            case HOME:
                return homes.get(color).get(location);
        }
        return null;
    }

//    public boolean invariant(int pos) {
//        if (ring[pos].first != null && ring[pos].second != null) {
//            return ring[pos].first.color == ring[pos].second.color;
//        }
//        return true;
//    }
//    // clears location and returns pawns
//    public Pair<Pawn, Pawn> clearCell(int i) {
//        Pair<Pawn, Pawn> set = new Pair();
//        set.first = ring[i].first;
//        set.second = ring[i].second;
//        ring[i] = new Pair();
//        if (!invariant(i)) {
//            return null;
//        }
//        return set;
//    }
//
//    remove a pawn by id
//    public boolean remove(int location, String color, int id) {
//        if (ring[location].first.color == color && ring[location].first.id == id) {
//            ring[location].first = null;
//            return true;
//        } else if (ring[location].second.color == color && ring[location].second.id == id) {
//            ring[location].second = null;
//            return true;
//        }
//
//        if (!invariant(location)) {
//            return false;
//        }
//
//        return false;
//
//    }
//
//    returns boolean if should bop
//    public int bopLoc(int location, String color) {
//        //assume no blockade
//        boolean clear1 = ring[location].first == null;
//        boolean clear2 = ring[location].second == null;
//
//        if (clear1 && clear2) {
//            return -1;
//        }
//
//        if (clear1 && ring[location].second.color != color) {
//            return 1;
//        }
//        if (clear2 && ring[location].first.color != color) {
//            return 0;
//        }
//
//        if (!invariant(location)) {
//            return -1;
//        }
//
//        return -1;
//        //checks if inserting color at loc will bop
//    }
//
//  sends bop target home
//    public Pawn bopTarget(int location, int index) {
//        for (int loc : SAFE_LOCATIONS) {
//            if (loc == location) {
//                return null;
//            }
//        }
//        if (!invariant(location)) {
//            return null;
//        }
//
//        Pawn copy = new Pawn(-1, null);
//        if (index == 0) {
//            copy.color = ring[location].first.color;
//            ring[location].first = null;
//        } else if (index == 1) {
//            copy.color = ring[location].second.color;
//            ring[location].second = null;
//        }
//        return copy;
//    }
//
//    inserts pawn at location
//    public boolean add(int location, String color, int id) {
//        //bops if needed
//        boolean clear1 = ring[location].first == null;
//        boolean clear2 = ring[location].second == null;
//        if (clear1) {
//            ring[location].first = new Pawn(id, color);
//            ring[location].first.home = false;
//            ring[location].first.location = location;
//            return true;
//        } else if (clear2) {
//            ring[location].second = new Pawn(id, color);
//            ;
//            ring[location].first.home = false;
//            ring[location].first.location = location;
//            return true;
//        }
//
//        if (!invariant(location)) {
//            return false;
//        }
//
//        return false;
//    }

//
//    public class Runway {
//        String color;
//        int pieces;
//        Object[] runway = new Object[6];
//        Pawn[] endZone = new Pawn[4];
//
//        public Runway(String color) {
//            this.color = color;
//            pieces = 0;
//
//            for (int i = 0; i < 6; i++) {
//                runway[i] = new Pair<>();
//            }
//        }
//

//        public boolean remove(int location, String color, int id) {
//            if (runway[location].first.color == color && runway[location].first.id == id) {
//                runway[location].first = null;
//                return true;
//            } else if (runway[location].second.color == color && runway[location].second.id == id) {
//                runway[location].second = null;
//                return true;
//            }
//            return false;
//        }
//
//        public boolean add(int location, String color, int id) {
//            if (location == 6) {
//                for (int i = 0; i < 4; i++) {
//                    if (endZone[i] == null) {
//                        endZone[i] = new Pawn(id, color);
//                    }
//                }
//                return true;
//            }
//            //add to endzone
//            else {
//                boolean clear1 = runway[location].first == null;
//                boolean clear2 = runway[location].second == null;
//                if (clear1) {
//                    runway[location].first = new Pawn(id, color);
//                    return true;
//                } else if (clear2) {
//                    runway[location].second = new Pawn(id, color);
//                    return true;
//                }
//                return false;
//            }
//        }
//    }
}
