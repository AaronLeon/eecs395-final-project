package parcheesi;

// represents a move where a player enters a piece
public class EnterPiece implements Move {
  public Pawn pawn;
  public EnterPiece(Pawn pawn) {
    this.pawn=pawn;
  }

  public boolean equals(Object other) {
    if (!(other instanceof EnterPiece)) {
      return false;
    }
    return this.pawn.equals(((EnterPiece) other).pawn);
  }

}

