import java.util.ArrayList;
import java.util.Arrays;
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
            put("blue", 4);
            put("yellow", 21);
            put("green", 38);
            put("red", 55);
        }
    };
    static final HashMap<String, Integer> HOMEROW_LOCATIONS = new HashMap<String, Integer>() {
        {
            put("blue", 67);
            put("yellow", 16);
            put("green", 33);
            put("red", 50);
        }
    };
    static final int SAFE_LOCATIONS[] = {4, 11, 16, 21, 28, 33, 38, 45, 50, 55, 62, 67};


    Object[] ring;
    HashMap<String, Object[]> nests;
    HashMap<String, Object[]> homeRows;
    HashMap<String, Object[]> homes;
    HashMap<String, Pawn[]> pawns;

    public Board() {
        pawns = new HashMap<>(4);
        ring = new Object[RING_SIZE];
        nests = new HashMap<>(4);
        homeRows = new HashMap<>(4);
        homes = new HashMap<>(4);

        for (String color: COLORS) {
            Pawn[] temp = new Pawn[4];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = new Pawn(i, color);
            }

            pawns.put(color, temp);
            nests.put(color, new Object[NEST_SIZE]);
            homeRows.put(color, temp);
        }
    }

    public boolean isSafe(int location) {
        for (int safe: SAFE_LOCATIONS) {
            if (location == safe) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlockade(BoardComponent bc, int location, String color) {
        switch (bc) {
            case RING:
                return ring[location] instanceof Pair;
            case HOMEROW:
                return homes.get(color)[location] instanceof Pair;
        }
        return false;
    }

    void sendBackToNest(Pawn pawn) {
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
        pawn.location = pawn.id;
        nests.get(pawn.color)[pawn.id] = pawn;
    }

    public void enterPiece(Pawn pawn) {
        if (pawn.bc != BoardComponent.NEST) {
            return;
        }
        int homeLocation = NEST_LOCATIONS.get(pawn.color);
        nests.get(pawn.color)[pawn.location] = null;
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
        pawn.location = pawn.id;
        homes.get(pawn.color)[pawn.id] = pawn;
    }

    public Pawn extractPawnFromBlockade(Pawn pawn, Blockade blockade) throws Exception {
        if (pawn.bc != blockade.first.bc || pawn.bc != blockade.second.bc
                || pawn.location != blockade.first.location || pawn.location != blockade.second.location) {
            throw new Exception("Trying to extract pawn from blockade that is not in same location");
        }

        BoardComponent bc = pawn.bc;
        int location = pawn.location;
        String color = pawn.color;
        if (pawn.equals(blockade.first)) {
            set(blockade.second, bc, location, color);
            return pawn;
        }
        else if (pawn.equals(blockade.second)) {
            set(blockade.first, bc, location, color);
            return pawn;
        }
        throw new Exception("Trying to extract pawn from blockade that it does not belong to");
    }

    public void formBlockade(Pawn p1, Pawn p2) throws Exception {
        if (!p1.color.equals(p2.color)) {
            throw new Exception("Trying to form invalid blockade with different colors!");
        }
        else if (p1.bc != p2.bc || p1.location != p2.location) {
            throw new Exception("Trying to form invalid blockade with pawns located in different locations");
        }
        Blockade blockade = new Blockade(p1, p2);
        set(blockade, p1.bc, p1.location, p1.color);
    }

    public Object get(BoardComponent bc, int location, String color) {
        switch(bc) {
            case RING:
                return ring[location];
            case NEST:
                return nests.get(color)[location];
            case HOMEROW:
                return homeRows.get(color)[(location)];
            case HOME:
                return homes.get(color)[location];
        }
        return null;
    }

    public Object set(Object obj, BoardComponent bc, int location, String color) {
        switch(bc) {
            case RING:
                ring[location] = obj;
            case HOMEROW:
                homeRows.get(color)[(location)] = obj;
        }
        return null;
    }
}
