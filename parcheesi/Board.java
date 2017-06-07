package parcheesi;

import java.util.Arrays;
import java.util.HashMap;

import static parcheesi.Board.BoardComponent.NEST;

public class Board {
    public enum BoardComponent {
        NEST, RING, HOMEROW, HOME
    }

    public static final String[] COLORS = {"blue", "yellow", "green", "red"} ;
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


    public BoardObject[] ring;
    public HashMap<String, BoardObject[]> nests;
    public HashMap<String, BoardObject[]> homeRows;
    public HashMap<String, BoardObject[]> homes;
    public HashMap<String, Pawn[]> pawns;

    public Board() {
        ring = new BoardObject[RING_SIZE];
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
            nests.put(color, new BoardObject[NEST_SIZE]);
            homeRows.put(color, new BoardObject[HOMEROW_SIZE]);
            homes.put(color, new BoardObject[HOME_SIZE]);
        }
    }

    public void initPawns() {
        for (String color: COLORS) {
            for (int i = 0; i < NEST_SIZE; ++i) {
                nests.get(color)[i] = new Pawn(i, color);
            }
        }
    }

    public static boolean isSafe(int location) {
        for (int safe: SAFE_LOCATIONS) {
            if (location == safe) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlockade(Location location, String color) {
        switch (location.bc) {
            case RING:
                return ring[location.index] instanceof Blockade;
            case HOMEROW:
                return homeRows.get(color)[location.index] instanceof Blockade;
        }
        return false;
    }

    void sendBackToNest(Pawn pawn) {
        if (pawn.location.bc == BoardComponent.RING) {
            ring[pawn.location.index] = null;
        }
        else if (pawn.location.bc == BoardComponent.HOMEROW) {
            homeRows.get(pawn.color)[pawn.location.index] = null;
        }
        else {
            return;
        }
        pawn.location = new Location(BoardComponent.NEST, pawn.id);
        nests.get(pawn.color)[pawn.id] = pawn;
    }

    public void enterPiece(Pawn pawn) throws Exception {
        if (pawn.location.bc != NEST) {
            throw new Exception("parcheesi.Pawn that is not in nest is trying to enter board");
        }
        int homeLocation = NEST_LOCATIONS.get(pawn.color);
        nests.get(pawn.color)[pawn.location.index] = null;
        pawn.location = new Location(BoardComponent.RING, homeLocation);

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
        int newLocation = pawn.location.index + distance;

        // Check if breaking pawn from blockade
        if (ring[pawn.location.index] instanceof Blockade) {
            breakBlockade(pawn, (Blockade) ring[pawn.location.index]);
        }
        else {
            ring[pawn.location.index] = null;
        }

        if (newLocation > homeRowLocation) {
            int newDistance = (newLocation % homeRowLocation) - 1;
            pawn.location = new Location(BoardComponent.HOMEROW, 0);
            return movePawnHomeRow(pawn, newDistance);
        }
        pawn.location.index = newLocation;

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
        int newLocation = pawn.location.index + distance;
        if (newLocation == HOMEROW_SIZE) {
            movePawnHome(pawn);
            return true;
        }

        // Check if breaking pawn from blockade
        if (homeRows.get(pawn.color)[pawn.location.index] instanceof Blockade) {
            breakBlockade(pawn, (Blockade) homeRows.get(pawn.color)[pawn.location.index]);
        }
        else {
            homeRows.get(pawn.color)[pawn.location.index] = null;
        }
        pawn.location.index = newLocation;

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
        if (pawn.location.bc == BoardComponent.RING) {
            ring[pawn.location.index] = null;
        }
        else if (pawn.location.bc == BoardComponent.HOMEROW){
            homeRows.get(pawn.color)[pawn.location.index] = null;
        }
        pawn.location = new Location(BoardComponent.HOME, pawn.id);
        homes.get(pawn.color)[pawn.id] = pawn;
    }

    public Pawn breakBlockade(Pawn pawn, Blockade blockade) throws Exception {
        if (!pawn.location.equals(blockade.first().location)
                || !pawn.location.equals(blockade.second().location)) {
            throw new Exception("Trying to extract pawn from blockade that is not in same location");
        }

        Location location = pawn.location;
        String color = pawn.color;
        if (pawn.equals(blockade.first())) {
            set(blockade.second(), location, color);
            return pawn;
        }
        else if (pawn.equals(blockade.second())) {
            set(blockade.first(), location, color);
            return pawn;
        }
        else throw new Exception("Trying to extract pawn from blockade that it does not belong to");
    }

    public void formBlockade(Pawn p1, Pawn p2) throws Exception {
        if (!p1.color.equals(p2.color)) {
            throw new Exception("Trying to form invalid blockade with different colors!");
        }
        else if (!p1.location.equals(p2.location)) {
            throw new Exception("Trying to form invalid blockade with pawns located in different locations");
        }
        Blockade blockade = new Blockade(p1, p2);
        set(blockade, p1.location, p1.color);
    }

    public BoardObject get(Location location, String color) {
        switch(location.bc) {
            case RING:
                return ring[location.index];
            case NEST:
                return nests.get(color)[location.index];
            case HOMEROW:
                return homeRows.get(color)[location.index];
            case HOME:
                return homes.get(color)[location.index];
        }
        return null;
    }

    public void set(BoardObject obj, Location location, String color) {
        switch(location.bc) {
            case RING:
                ring[location.index] = obj;
                break;
            case HOMEROW:
                homeRows.get(color)[location.index] = obj;
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
            if (!Arrays.equals(nests.get(color), b.nests.get(color))
                || !Arrays.equals(homeRows.get(color), b.homeRows.get(color))
                || !Arrays.equals(homes.get(color), b.homes.get(color))) {
                return false;
            }
        }
        return true;
    }
}
