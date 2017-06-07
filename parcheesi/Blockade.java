package parcheesi;

public class Blockade extends BoardObject {
    Pair<Pawn, Pawn> pawns;
    public Blockade(Pawn first, Pawn second) {
        this.pawns = new Pair<>(first, second);
    }

    public Pawn first() {
        return pawns.first;
    }

    public Pawn second() {
        return pawns.second;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Blockade)) {
            return false;
        }
        Blockade b = (Blockade) other;
        return b.pawns.equals(b.pawns);
    }
}
