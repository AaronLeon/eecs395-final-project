package parcheesi;

import java.util.Arrays;
import java.util.HashMap;

public class Board {
    public enum BoardComponent {
        NEST, RING, HOMEROW, HOME
    }

    public static final String[] COLORS = {"blue", "yellow", "green", "red"};
    public static final int RING_SIZE = 17 * 4;
    public static final int HOMEROW_SIZE =  7;
    public static final int NEST_SIZE = 4;
    public static final int HOME_SIZE = 4;

    public static final HashMap<String, Integer> NEST_LOCATIONS = new HashMap<String, Integer>() {
        {
            put("blue", 4);
            put("yellow", 21);
            put("green", 38);
            put("red", 55);
        }
    };
    public static final HashMap<String, Integer> HOMEROW_LOCATIONS = new HashMap<String, Integer>() {
        {
            put("blue", 67);
            put("yellow", 16);
            put("green", 33);
            put("red", 50);
        }
    };
    public static final int SAFE_LOCATIONS[] = {4, 11, 16, 21, 28, 33, 38, 45, 50, 55, 62, 67};


    public Object[] ring;
    public HashMap<String, Object[]> nests;
    public HashMap<String, Object[]> homeRows;
    public HashMap<String, Object[]> homes;
    //map color to string of pawns
    public HashMap<String, Pawn[]> pawns;

    public Board() {
        ring = new Object[RING_SIZE];
        pawns = new HashMap<>(4);
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
            homeRows.put(color, new Object[HOMEROW_SIZE]);
            homes.put(color, new Object[HOME_SIZE]);
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
                return ring[location] instanceof Blockade;
            case HOMEROW:
                return homeRows.get(color)[location] instanceof Blockade;
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

    public void enterPiece(Pawn pawn) throws Exception {
        if (pawn.bc != BoardComponent.NEST) {
            throw new Exception("parcheesi.Pawn that is not in nest is trying to enter board");
        }
        int homeLocation = NEST_LOCATIONS.get(pawn.color);
        nests.get(pawn.color)[pawn.location] = null;
        pawn.bc = BoardComponent.RING;
        pawn.location = homeLocation;

        // Check if forming new blockade
        if (ring[homeLocation] instanceof Pawn) {
            formBlockade(pawn, (Pawn) ring[homeLocation]);
        }
        else {
            ring[homeLocation] = pawn;
        }
    }

    public boolean movePawnRing(Pawn pawn, int distance) throws Exception {
        int homeRowLocation = HOMEROW_LOCATIONS.get(pawn.color);
        int newLocation = pawn.location + distance;

        // Check if breaking pawn from blockade
        if (ring[pawn.location] instanceof Blockade) {
            breakBlockade(pawn, (Blockade) ring[pawn.location]);
        }
        else {
            ring[pawn.location] = null;
        }

        if (newLocation > homeRowLocation) {
            int newDistance = (newLocation % homeRowLocation) - 1;
            pawn.bc = BoardComponent.HOMEROW;
            pawn.location = 0;
            return movePawnHomeRow(pawn, newDistance);
        }
        pawn.location = newLocation;

        // Check if forming new blockade
        if (ring[newLocation] instanceof Pawn) {
            formBlockade(pawn, (Pawn) ring[newLocation]);
        }
        else {
            ring[newLocation] = pawn;
        }
        return false;
    }

    public boolean movePawnHomeRow(Pawn pawn, int distance) throws Exception {
        int newLocation = pawn.location + distance;
        if (newLocation == HOMEROW_SIZE) {
            movePawnHome(pawn);
            return true;
        }

        // Check if breaking pawn from blockade
        if (homeRows.get(pawn.color)[pawn.location] instanceof Blockade) {
            breakBlockade(pawn, (Blockade) homeRows.get(pawn.color)[pawn.location]);
        }
        else {
            homeRows.get(pawn.color)[pawn.location] = null;
        }
        pawn.location = newLocation;

        // Check if forming  blockade
        if (homeRows.get(pawn.color)[newLocation] instanceof Pawn) {
            formBlockade(pawn, (Pawn) homeRows.get(pawn.color)[newLocation]);
        }
        else  {
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

    public Pawn breakBlockade(Pawn pawn, Blockade blockade) throws Exception {
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
        else throw new Exception("Trying to extract pawn from blockade that it does not belong to");
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

    public void set(Object obj, BoardComponent bc, int location, String color) {
        switch(bc) {
            case RING:
                ring[location] = obj;
                break;
            case HOMEROW:
                homeRows.get(color)[(location)] = obj;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Board)) {
            return false;
        }
        Board b = (Board) other;


        if (!Arrays.equals(ring, b.ring)) {
            return false;
        }
        for (String color : COLORS) {
            if (Arrays.equals(nests.get(color), b.nests.get(color))
                || Arrays.equals(homeRows.get(color), b.homeRows.get(color))
                || Arrays.equals(homes.get(color), b.homes.get(color))) {
                return false;
            }
        }
        return true;
    }
}
