package parcheesi;

// represents a move that starts on one of the home rows
public class MoveHome implements Move {
  public Pawn pawn;
  public int start;
  public int distance;

  public MoveHome(Pawn pawn, int distance) {
    this.pawn=pawn;
    this.distance=distance;
  }

  public void makeMove(){
    return;
  }
}

