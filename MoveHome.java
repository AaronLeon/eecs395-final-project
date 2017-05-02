// represents a move that starts on one of the home rows
public class MoveHome implements Move {
  Pawn pawn;
  int start;
  int distance;

  MoveHome(Pawn pawn, int distance) {
    this.pawn=pawn;
    this.distance=distance;
  }

  public void makeMove(){
    return;
  }
}

