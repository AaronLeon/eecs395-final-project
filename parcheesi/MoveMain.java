package parcheesi;

// represents a move that starts on the main ring
// (but does not have to end up there)
public class MoveMain implements Move {
  public Pawn pawn;
  public int distance;

  public MoveMain(Pawn pawn, int distance) {
    this.pawn=pawn;
    this.distance=distance;
  }
}
