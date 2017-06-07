package parcheesi;

public class Location {
    public Board.BoardComponent bc;
    public int index;

    public Location(Board.BoardComponent bc, int index) {
        this.bc = bc;
        this.index = index;
    }

    public boolean equals(Object other) {
        if (! (other instanceof Location)) {
            return false;
        }

        Location loc = (Location) other;
        return this.bc == loc.bc
                && this.index == loc.index;
    }
}
